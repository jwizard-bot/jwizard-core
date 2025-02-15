package pl.jwizard.jwc.audio.client

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import pl.jwizard.jwac.AudioClient
import pl.jwizard.jwac.AudioNodeListener
import pl.jwizard.jwac.AudioSessionController
import pl.jwizard.jwac.gateway.GatewayVoiceStateInterceptor
import pl.jwizard.jwac.gateway.JDAVoiceUpdateListener
import pl.jwizard.jwac.node.AudioNode
import pl.jwizard.jwac.node.NodeConfig
import pl.jwizard.jwac.node.NodePool
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.audio.spi.DistributedAudioClient
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.CleanupAfterIoCDestroy
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.node.AudioNodeDefinition
import pl.jwizard.jwl.node.AudioNodesCache
import pl.jwizard.jwl.util.logger

@SingletonComponent
class DistributedAudioClientBean(
	private val environment: EnvironmentBean,
	private val audioNodeListener: AudioNodeListener,
	private val gatewayVoiceStateInterceptor: GatewayVoiceStateInterceptor,
) : DistributedAudioClient, CleanupAfterIoCDestroy {
	companion object {
		private val log = logger<DistributedAudioClientBean>()
	}

	private val audioServerTimeout = environment
		.getProperty<Long>(BotProperty.AUDIO_SERVER_TIMEOUT_MS)

	private lateinit var client: AudioClient
	private lateinit var audioNodesCache: AudioNodesCache
	private lateinit var audioController: AudioSessionController

	val availableNodes
		get() = client.getNodes(onlyAvailable = true)

	override fun initClient() {
		val jdaSecretToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
		val instanceName = environment.getProperty<String>(BotProperty.JDA_INSTANCE_NAME)

		audioNodesCache = AudioNodesCache(environment)
		client = AudioClient(jdaSecretToken, instanceName, audioNodeListener)
		audioController = AudioSessionController(client, gatewayVoiceStateInterceptor)

		reloadOrInitNodes()
		client.initAudioEventListeners()
	}

	override fun getPlayersCountInSelectedGuilds(
		guilds: List<Guild>,
	) = client.getPlayersCountForSelectedGuilds(guilds.map(Guild::getIdLong))


	override fun destroy() {
		log.info("Closing Lavalink client...")
		client.close()
	}

	override val voiceDispatchInterceptor
		get() = JDAVoiceUpdateListener(client)

	fun reloadOrInitNodes(): Pair<Int, Int> {
		val prevNodes = audioNodesCache.nodes.map { it.name }
		client.removeNodes(prevNodes)
		audioNodesCache.fetchAndReloadNodes()

		val nodeConfigs = audioNodesCache.nodes
			.filter(AudioNodeDefinition::active)
			.map {
				NodeConfig.Builder()
					.setHostDescriptor(it.name, it.password)
					.setAddress(it.host, it.port, it.secure)
					.setBalancerSetup(AudioNodeType.valueOf(it.nodePool), it.regionGroup)
					.setHttpTimeout(audioServerTimeout)
					.build()
			}
		client.addNodes(nodeConfigs)
		return Pair(prevNodes.size, nodeConfigs.size)
	}

	fun getLink(guildId: Long) = client.getLinkIfCached(guildId)

	fun loadAndTransferToNode(
		context: GuildCommandContext,
		pool: NodePool,
		onTransfer: (AudioNode) -> Unit,
	): Boolean =
		audioController.loadAndTransferToNode(
			context.guild,
			pool,
			context.author,
			context.selfMember,
			onTransfer
		)

	fun disconnectWithAudioChannel(guild: Guild) = audioController.disconnectWithAudioChannel(guild)

	fun inAudioChannel(member: Member) = gatewayVoiceStateInterceptor.inAudioChannel(member) == true
}
