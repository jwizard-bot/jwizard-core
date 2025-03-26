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
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.util.logger
import pl.jwizard.jwl.vault.VaultClient

@Component
class DistributedAudioClientImpl(
	private val environment: BaseEnvironment,
	private val audioNodeListener: AudioNodeListener,
	private val gatewayVoiceStateInterceptor: GatewayVoiceStateInterceptor,
) : DistributedAudioClient, DisposableBean {
	companion object {
		private val log = logger<DistributedAudioClientImpl>()
	}

	private val vaultClient = VaultClient(environment)
	private val audioServerTimeout =
		environment.getProperty<Long>(BotProperty.AUDIO_SERVER_TIMEOUT_MS)

	private val proxyVerificationHeaderName = environment
		.getProperty<String>(AppBaseProperty.PROXY_VERIFICATION_HEADER_NAME)
	private val proxyVerificationToken = environment
		.getProperty<String>(AppBaseProperty.PROXY_VERIFICATION_TOKEN)

	private lateinit var client: AudioClient
	private lateinit var audioController: AudioSessionController

	val availableNodes
		get() = client.getNodes(onlyAvailable = true)

	override fun initClient() {
		val jdaSecretToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
		val instanceName = environment.getProperty<String>(BotProperty.JDA_INSTANCE_NAME)
		val shardStart = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_START)
		val shardEnd = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_END)

		client = AudioClient(
			jdaSecretToken,
			instanceName = "$instanceName-f${shardStart}t$shardEnd",
			audioNodeListener,
		)
		initOrReloadNodes()
		audioController = AudioSessionController(client, gatewayVoiceStateInterceptor)
		client.initAudioEventListeners()
	}

	fun initOrReloadNodes() {
		vaultClient.initOnce()
		// fetch available audio nodes from vault client as audio-nodes/N where N is the node id
		val audioNodes = vaultClient.readKvGroupPropertySource<Int, AudioNodeProperty>(
			kvPath = "audio-node",
			patternFilter = Regex("^\\d+$"),
			keyExtractor = { it.toInt() }
		)
		val activeAudioNodes = audioNodes.values
			.filter { it.get(AudioNodeProperty.ACTIVE) } // take only active nodes
			.map {
				NodeConfig.Builder()
					.setHostDescriptor(
						name = it.get(AudioNodeProperty.NAME),
						password = it.get(AudioNodeProperty.PASSWORD),
					)
					.setAddress(
						hostWithPort = it.get(AudioNodeProperty.GATEWAY_HOST),
						secure = it.get(AudioNodeProperty.SECURE),
					)
					.setBalancerSetup(
						pool = AudioNodeType.valueOf(it.get(AudioNodeProperty.NODE_POOL)),
						regionGroup = it.get(AudioNodeProperty.REGION_GROUP)
					)
					.setHttpTimeout(audioServerTimeout)
					.setProxyVerificationToken(
						protected = it.get(AudioNodeProperty.PROXY_PROTECT),
						headerName = proxyVerificationHeaderName,
						token = proxyVerificationToken,
					)
					.build()
			}
		client.removeAllNodes()
		client.addNodes(activeAudioNodes)
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
