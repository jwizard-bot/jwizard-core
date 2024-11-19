/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor

/**
 * Interface for supplying a distributed audio client, providing methods to manage audio nodes and handle voice
 * dispatching.
 *
 * @author Miłosz Gilga
 */
interface AudioClient {

	/**
	 * Initializes the client nodes for distributed audio processing. This method should set up the necessary connections
	 * and configurations for the audio nodes.
	 */
	fun initClient()

	/**
	 * Provides the voice dispatch interceptor used for managing voice state updates
	 * and dispatching audio events.
	 */
	val voiceDispatchInterceptor: VoiceDispatchInterceptor
}
