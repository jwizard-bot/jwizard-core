package pl.jwizard.jwc.audio.client

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import pl.jwizard.jwc.audio.gateway.AudioClient
import pl.jwizard.jwc.audio.gateway.AudioNodeListener
import pl.jwizard.jwc.audio.gateway.AudioSessionController
import pl.jwizard.jwc.audio.gateway.discord.GatewayVoiceStateInterceptor
import pl.jwizard.jwc.audio.gateway.discord.JDAVoiceUpdateListener
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.node.NodeConfig
import pl.jwizard.jwc.audio.gateway.node.NodePool
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.audio.DistributedAudioClient
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwl.node.AudioNodeDefinition
import pl.jwizard.jwl.node.AudioNodesCache
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.util.logger

@Component
class DistributedAudioClientImpl(
	private val environment: BaseEnvironment,
	private val audioNodeListener: AudioNodeListener,
	private val gatewayVoiceStateInterceptor: GatewayVoiceStateInterceptor,
) : DistributedAudioClient, DisposableBean {
	companion object {
		private val log = logger<DistributedAudioClientImpl>()
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
		val shardStart = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_START)
		val shardEnd = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_END)

		audioNodesCache = AudioNodesCache(environment)
		client = AudioClient(
			jdaSecretToken,
			instanceName = "$instanceName-f${shardStart}t$shardEnd",
			audioNodeListener,
		)
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
