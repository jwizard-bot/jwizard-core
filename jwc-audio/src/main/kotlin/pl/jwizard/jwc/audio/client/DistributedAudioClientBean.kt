/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.client

import net.dv8tion.jda.api.entities.Guild
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

/**
 * Component responsible for managing audio client and nodes, handling audio connections and interactions for different
 * guilds in a distributed manner.
 *
 * @property environment Provides access to environment configurations and properties.
 * @property audioNodeListener Listener for handling various audio server events.
 * @property gatewayVoiceStateInterceptor
 * @author Miłosz Gilga
 */
@SingletonComponent
class DistributedAudioClientBean(
	private val environment: EnvironmentBean,
	private val audioNodeListener: AudioNodeListener,
	private val gatewayVoiceStateInterceptor: GatewayVoiceStateInterceptor,
) : DistributedAudioClient, CleanupAfterIoCDestroy {

	companion object {
		private val log = logger<DistributedAudioClientBean>()
	}

	/**
	 * Timeout in milliseconds for communication with the audio server.
	 */
	private val audioServerTimeout = environment.getProperty<Long>(BotProperty.AUDIO_SERVER_TIMEOUT_MS)

	/**
	 * The audio client instance used to manage audio connections and playback.
	 */
	private lateinit var client: AudioClient

	/**
	 * Cache containing metadata and definitions for all audio nodes.
	 */
	private lateinit var audioNodesCache: AudioNodesCache

	/**
	 * Controller for managing audio sessions, including playback and interactions with the audio client.
	 */
	private lateinit var audioController: AudioSessionController

	/**
	 * Initializes the audio client and necessary components, such as the session controller and node cache. Sets up
	 * audio event listeners and loads nodes into the client.
	 */
	override fun initClient() {
		val jdaSecretToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
		val instanceName = environment.getProperty<String>(BotProperty.JDA_INSTANCE_NAME)

		audioNodesCache = AudioNodesCache(environment)
		client = AudioClient(jdaSecretToken, instanceName, audioNodeListener)
		audioController = AudioSessionController(client, gatewayVoiceStateInterceptor)

		reloadOrInitNodes()
		client.initAudioEventListeners()
	}

	/**
	 * Reloads or initializes audio nodes from the cache. Removes old nodes, fetches the latest node definitions, and adds
	 * active nodes to the client.
	 *
	 * @return A pair representing the count of nodes removed and added, respectively.
	 */
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
					.setBalancerSetup(AudioNodePool.valueOf(it.nodePool), it.regionGroup)
					.setHttpTimeout(audioServerTimeout)
					.build()
			}
		client.addNodes(nodeConfigs)
		return Pair(prevNodes.size, nodeConfigs.size)
	}

	/**
	 * Retrieves a cached link for a specific guild ID if it exists.
	 *
	 * @param guildId The ID of the guild.
	 * @return The cached link if available, otherwise null.
	 */
	fun getLink(guildId: Long) = client.getLinkIfCached(guildId)

	/**
	 * Loads and transfers the audio node for a guild to a specified node pool. Executes a callback after the
	 * transfer is complete.
	 *
	 * @param context The command context containing guild and author information.
	 * @param pool The target node pool to transfer to.
	 * @param onTransfer A callback executed after transferring to the target node.
	 */
	fun loadAndTransferToNode(context: GuildCommandContext, pool: NodePool, onTransfer: (AudioNode) -> Unit) =
		audioController.loadAndTransferToNode(context.guild, pool, context.author, context.selfMember, onTransfer)

	/**
	 * Disconnects the bot from the audio channel in the specified guild.
	 *
	 * @param guild The guild from which the bot should disconnect.
	 */
	fun disconnectWithAudioChannel(guild: Guild) = audioController.disconnectWithAudioChannel(guild)

	/**
	 * Cleans up resources by disposing of all event handlers and closing the Lavalink client.
	 */
	override fun destroy() {
		log.info("Closing Lavalink client...")
		client.close()
	}

	/**
	 * Returns a voice dispatch interceptor that interacts with JDA to handle voice updates for the Lavalink client.
	 */
	override val voiceDispatchInterceptor
		get() = JDAVoiceUpdateListener(client)

	/**
	 * Retrieves a list of currently available Lavalink nodes that can handle audio streams. These nodes are responsible
	 * for processing and distributing audio playback across guilds.
	 */
	val availableNodes
		get() = client.getNodes(onlyAvailable = true)
}
