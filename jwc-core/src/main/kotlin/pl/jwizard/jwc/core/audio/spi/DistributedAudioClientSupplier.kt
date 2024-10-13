/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

import dev.arbjerg.lavalink.client.LavalinkNode
import dev.arbjerg.lavalink.client.Link
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor

/**
 * Interface for supplying a distributed audio client, providing methods to manage audio nodes and handle voice
 * dispatching.
 *
 * @author Miłosz Gilga
 */
interface DistributedAudioClientSupplier {

	/**
	 * Initializes the client nodes for distributed audio processing. This method should set up the necessary connections
	 * and configurations for the audio nodes.
	 */
	fun initClientNodes()

	/**
	 * Retrieves or creates a link for the specified guild.
	 *
	 * @param guildId The ID of the guild for which to get or create the audio link.
	 * @return The audio link associated with the specified guild.
	 */
	fun getOrCreateLink(guildId: Long): Link

	/**
	 * Provides the voice dispatch interceptor used for managing voice state updates
	 * and dispatching audio events.
	 */
	val voiceDispatchInterceptor: VoiceDispatchInterceptor

	/**
	 * Retrieves a list of currently available Lavalink nodes that can handle audio streams. These nodes are responsible
	 * for processing and distributing audio playback across guilds.
	 */
	val availableNodes: List<LavalinkNode>
}
