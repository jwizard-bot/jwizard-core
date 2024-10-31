/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api

import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.core.audio.AudioContentType
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.radio.DiscreteAudioStreamIsPlayingException
import pl.jwizard.jwc.exception.radio.RadioStationIsNotPlayingException
import pl.jwizard.jwc.exception.radio.RadioStationIsPlayingException

/**
 * Base class for handling radio-related commands. It extends the [AudioCommandBase] class and provides a common
 * structure for executing radio commands in an audio environment.
 *
 * @param commandEnvironment The environment configuration for commands, including dependencies and caches.
 * @author Miłosz Gilga
 */
abstract class RadioCommandBase(commandEnvironment: CommandEnvironmentBean) : AudioCommandBase(commandEnvironment) {

	protected val radioPlaybackMappersCache = commandEnvironment.radioPlaybackMappersCache

	/**
	 * Executes the audio-related functionality of the radio command, including checking the user's voice state,
	 * determining if a discrete audio stream is already playing, and verifying if a radio station is playing or idle.
	 *
	 * @param context The context of the command, containing information about the guild, user, and channel.
	 * @param manager The music manager that handles audio playback.
	 * @param response The response object for handling future actions or interactions.
	 * @throws DiscreteAudioStreamIsPlayingException If a discrete audio stream is already playing.
	 * @throws RadioStationIsNotPlayingException If a command requires the radio to be playing, but it is not.
	 * @throws RadioStationIsPlayingException If a command requires the radio to be idle, but it is playing.
	 */
	final override fun executeAudio(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val voiceState = checkUserVoiceState(context)
		userIsWithBotOnAudioChannel(voiceState, context)
		val currentContent = manager.cachedPlayer?.track
		val isStreamContent = manager.state.isDeclaredAudioContentType(AudioContentType.STREAM)
		if (!isStreamContent && currentContent != null) {
			throw DiscreteAudioStreamIsPlayingException(context)
		}
		if (shouldRadioPlaying && (!isStreamContent || currentContent == null)) {
			throw RadioStationIsNotPlayingException(context)
		} else if (shouldRadioIdle && (isStreamContent && currentContent != null)) {
			throw RadioStationIsPlayingException(context)
		}
		executeRadio(context, manager, response)
	}

	/**
	 * Available only if radio is currently playing.
	 */
	protected open val shouldRadioPlaying = false

	/**
	 * Available only if radio is currently in idle mode (not playing).
	 */
	protected open val shouldRadioIdle = false

	/**
	 * Abstract function to be implemented by subclasses to execute the radio-specific command logic.
	 *
	 * @param context The context of the command.
	 * @param manager The music manager handling audio playback.
	 * @param response The future response object for deferred handling.
	 */
	protected abstract fun executeRadio(context: CommandContext, manager: MusicManager, response: TFutureResponse)
}
