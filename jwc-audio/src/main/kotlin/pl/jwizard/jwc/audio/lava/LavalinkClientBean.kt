/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.lava

import dev.arbjerg.lavalink.client.LavalinkClient
import dev.arbjerg.lavalink.client.LavalinkNode
import dev.arbjerg.lavalink.client.event.*
import dev.arbjerg.lavalink.client.getUserIdFromToken
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener
import pl.jwizard.jwc.core.audio.spi.AudioClient
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.CleanupAfterIoCDestroy
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger
import reactor.core.Disposable

/**
 * Component responsible for managing Lavalink client and nodes, handling audio connections and interactions for
 * different guilds in a distributed manner.
 *
 * @property environment Provides access to environment configurations and properties.
 * @property lavaNodeListener Listener for handling various Lavalink events.
 * @author Miłosz Gilga
 */
@SingletonComponent
class LavalinkClientBean(
	private val environment: EnvironmentBean,
	private val lavaNodeListener: LavaNodeListener,
) : AudioClient, CleanupAfterIoCDestroy {

	companion object {
		private val log = logger<LavalinkClientBean>()

		/**
		 * Separator used to split node definitions from environment properties.
		 */
		private const val NODE_DEFINITION_SEPARATOR = "::"
	}

	/**
	 * List of configured LavaNode instances representing different Lavalink nodes.
	 */
	private val lavaNodes: MutableList<LavaNode> = mutableListOf()

	/**
	 * List of disposable event handlers for cleaning up on shutdown.
	 */
	private val disposableNodes: MutableList<Disposable> = mutableListOf()

	/**
	 * The Lavalink client instance used to manage audio connections and playback.
	 */
	private lateinit var client: LavalinkClient

	init {
		val lavalinkNodes = environment.getListProperty<String>(BotListProperty.LAVALINK_NODES)
		val nodes = lavalinkNodes.map {
			val nodeDefinition = it.split(NODE_DEFINITION_SEPARATOR)
			LavaNode(
				name = nodeDefinition[0],
				regionGroup = RegionGroup.valueOf(nodeDefinition[1]),
				nodeToken = nodeDefinition[2],
				hostUrl = nodeDefinition[3],
			)
		}
		lavaNodes.addAll(nodes)
	}

	/**
	 * Initializes the Lavalink client nodes, sets up event listeners, and configures node load balancing based on
	 * regions.
	 */
	override fun initClient() {
		val jdaSecretToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
		val lavaTimeout = environment.getProperty<Long>(BotProperty.LAVALINK_TIMEOUT_MS)

		client = LavalinkClient(getUserIdFromToken(jdaSecretToken))
		client.loadBalancer.addPenaltyProvider(VoiceRegionPenaltyProvider())

		lavaNodes.forEach { client.addNode(it.toNodeOption(lavaTimeout)) }

		disposableNodes += client.on<TrackStartEvent>().subscribe(lavaNodeListener::onTrackStart)
		disposableNodes += client.on<TrackEndEvent>().subscribe(lavaNodeListener::onTrackEnd)
		disposableNodes += client.on<TrackStuckEvent>().subscribe(lavaNodeListener::onTrackStuck)
		disposableNodes += client.on<TrackExceptionEvent>().subscribe(lavaNodeListener::onTrackException)
		disposableNodes += client.on<WebSocketClosedEvent>().subscribe(lavaNodeListener::onCloseWsConnection)

		log.info("Init: {} Lava client nodes: {} and timeout: {}.", lavaNodes.size, lavaNodes, lavaTimeout)
	}

	/**
	 * Retrieves or creates a new audio link for a given guild. This manages the audio connection and playback for the
	 * specified guild.
	 *
	 * @param guildId The ID of the guild.
	 * @return The Lavalink link for the guild.
	 */
	fun getOrCreateLink(guildId: Long) = client.getOrCreateLink(guildId)

	/**
	 * Cleans up resources by disposing of all event handlers and closing the Lavalink client.
	 */
	override fun destroy() {
		disposableNodes.forEach { it.dispose() }
		log.info("Disposed: {} node event handlers.", disposableNodes.size)
		client.close()
		log.info("Closing Lavalink client...")
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
		get() = client.nodes.filter(LavalinkNode::available)
}
