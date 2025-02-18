package pl.jwizard.jwc.audio.gateway

import pl.jwizard.jwc.audio.gateway.balancer.DefaultLoadBalancer
import pl.jwizard.jwc.audio.gateway.balancer.LoadBalancer
import pl.jwizard.jwc.audio.gateway.balancer.region.VoiceRegion
import pl.jwizard.jwc.audio.gateway.event.ClientEvent
import pl.jwizard.jwc.audio.gateway.event.player.*
import pl.jwizard.jwc.audio.gateway.link.Link
import pl.jwizard.jwc.audio.gateway.link.LinkState
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.node.NodeConfig
import pl.jwizard.jwc.audio.gateway.node.NodePool
import pl.jwizard.jwc.audio.gateway.util.isEmpty
import pl.jwizard.jwc.audio.gateway.ws.ReconnectAudioNodeTask
import pl.jwizard.jwl.util.logger
import reactor.core.Disposable
import java.io.Closeable
import java.util.*
import java.util.concurrent.*

class AudioClient(
	secretToken: String,
	private val instanceName: String,
	private val listener: AudioNodeListener,
) : Closeable {
	companion object {
		private val log = logger<AudioClient>()
	}

	private val botId = getUserIdFromToken(secretToken)

	// map guild id to link
	private val internalLinks = ConcurrentHashMap<Long, Link>()
	private val internalNodes = CopyOnWriteArrayList<AudioNode>()

	// map current selected node pool in guild to guild id
	private val guildCurrentNodePool = ConcurrentHashMap<Long, NodePool>()

	private val disposables = mutableListOf<Disposable>()
	private val nodeDisposables = mutableMapOf<String, Disposable>()

	private val publisher = ReactiveEventPublisher<ClientEvent>()
	private val loadBalancer: LoadBalancer = DefaultLoadBalancer()

	// trigger updated discord voice gateway endpoint asynchronously when bot connecting to
	// voice channel
	internal var voiceGatewayUpdateTrigger: CompletableFuture<Void>? = null

	// true, if client is accepted client requests
	private var isOpen = true

	private val reconnectService = Executors.newSingleThreadScheduledExecutor {
		Thread(it, "audio-gateway-reconnect-thread").apply { isDaemon = true }
	}

	init {
		val task = ReconnectAudioNodeTask(botId, internalNodes)
		reconnectService.scheduleWithFixedDelay(task, 0, 500, TimeUnit.MILLISECONDS)
	}

	fun initAudioEventListeners() {
		disposables += publisher.ofType<KTrackStartEvent>().subscribe(listener::onTrackStart)
		disposables += publisher.ofType<KTrackEndEvent>().subscribe(listener::onTrackEnd)
		disposables += publisher.ofType<KTrackStuckEvent>().subscribe(listener::onTrackStuck)
		disposables += publisher.ofType<KTrackExceptionEvent>().subscribe(listener::onTrackException)
		disposables += publisher.ofType<KWsClosedEvent>().subscribe(listener::onCloseWsConnection)
		log.info("Init: {} audio client nodes: {}.", internalNodes.size, internalNodes)
	}

	private fun addNode(nodeConfig: NodeConfig) {
		if (internalNodes.any { it.config.name == nodeConfig.name }) {
			throw IllegalStateException("Node with name \"${nodeConfig.name}\" already exists.")
		}
		val node = AudioNode(nodeConfig, this, instanceName)
		node.connect(botId)
		nodeDisposables[node.name] =
			node.publisher.ofType<ClientEvent>().subscribe(publisher::publishWithException)
		internalNodes.add(node)
	}

	fun addNodes(nodeConfigs: List<NodeConfig>) {
		nodeConfigs.forEach(::addNode)
	}

	private fun removeNode(name: String): Boolean {
		val node = internalNodes.find { it.config.name == name }
		if (node == null) {
			log.debug("Unable to find node: {}. Skipping removable operation.", name)
			return false
		}
		if (node in internalNodes) {
			node.close()
			internalNodes.remove(node)
			val handler = nodeDisposables.remove(node.name)
			handler?.dispose()
			return true
		}
		return false
	}

	fun removeAllNodes() = internalNodes.forEach { removeNode(it.name) }

	fun getLinkIfCached(guildId: Long) = internalLinks[guildId]

	fun getOrCreateLink(
		guildId: Long,
		region: VoiceRegion? = null,
	): Link = internalLinks.getOrPut(guildId) {
		val pool = guildCurrentNodePool[guildId]
			?: throw IllegalStateException("Could not find guild: $guildId with linked node pool.")
		val nodesFromSelectedPool = internalNodes.filter { it.inNodePool(pool) }
		Link(guildId, loadBalancer.selectNode(nodesFromSelectedPool, region, guildId))
	}

	fun getNodes(onlyAvailable: Boolean = true) = if (onlyAvailable) {
		internalNodes.filter(AudioNode::available)
	} else {
		internalNodes
	}

	fun getPlayersCountForSelectedGuilds(
		guildIds: List<Long>,
	) = getNodes(onlyAvailable = true).sumOf {
		it.players.filterKeys { key -> guildIds.contains(key) }.size
	}

	internal fun onNodeDisconnected(audioNode: AudioNode) {
		if (!isOpen) {
			// ignore removing link when client is already disconnected
			return
		}
		val nodesFromPool = internalNodes.filter { it.inNodePool(audioNode.pool) }
		if (nodesFromPool.size == 1 && nodesFromPool.first() == audioNode) {
			internalLinks.forEach { (_, link) -> link.updateState(LinkState.DISCONNECTED) }
			return
		}
		if (nodesFromPool.all { !it.available }) {
			internalLinks
				.filter { (_, link) -> link.selectedNode == audioNode }
				.forEach { (_, link) -> link.updateState(LinkState.DISCONNECTED) }
			return
		}
		internalLinks.values
			.filter { it.selectedNode == audioNode }
			.forEach {
				it.transferNode(
					loadBalancer.selectNode(
						nodesFromPool, it.cachedPlayer?.voiceRegion,
						it.guildId
					)
				)
			}
	}

	internal fun removeDestroyedLink(guildId: Long) = internalLinks.remove(guildId)

	internal fun updateGuildNodePool(guildId: Long, pool: NodePool) {
		guildCurrentNodePool[guildId] = pool
	}

	internal fun transferNodeFromNewPool(
		guildId: Long,
		newPool: NodePool,
		afterSetNode: (AudioNode) -> Unit,
	) {
		val link = getLinkIfCached(guildId)
			?: throw IllegalStateException("Link for guild: $guildId does not exist.")
		if (link.selectedNode.inNodePool(newPool)) {
			log.info("Node is already in pool: {}. Skipping transfer.", newPool)
			afterSetNode(link.selectedNode)
			return
		}
		val nodeFromPool = internalNodes
			.find { it.pool == newPool }
			?: throw IllegalStateException("Could not find any node in pool: $newPool.")

		link.transferToPool(nodeFromPool, newPool, afterSetNode)
	}

	internal fun transferOrphansTo(audioNode: AudioNode) {
		if (!audioNode.available) {
			return
		}
		val unavailableNodes = internalNodes.filter { !it.available }
		val orphans = unavailableNodes.flatMap { it.players.values }
		orphans.mapNotNull { internalLinks[it.guildId] }
			.filter {
				!it.cachedPlayer?.voiceState.isEmpty() && audioNode.inNodePool(it.selectedNode.pool)
			}
			.forEach { it.transferNode(audioNode) }
	}

	private fun getUserIdFromToken(token: String) = try {
		val parts = token.split(".")
		if (parts.size != 3) {
			throw IllegalArgumentException("Token is not a valid bot token.")
		}
		String(Base64.getDecoder().decode(parts[0])).toLong()
	} catch (e: Exception) {
		throw IllegalArgumentException("Decoding failed: ${e.message}", e)
	}

	override fun close() {
		disposables.forEach { it.dispose() }
		log.info("Disposed: {} event handlers.", disposables.size)

		internalNodes.forEach { it.close() }
		nodeDisposables.values.forEach { it.dispose() }
		log.info("Disposed: {} audio nodes.", internalNodes)

		reconnectService.shutdownNow()
		publisher.dispose()
		isOpen = false
		log.info("Closing audio client...")
	}
}
