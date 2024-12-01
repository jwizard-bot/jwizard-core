/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.manager

import pl.jwizard.jwc.audio.AudioContentType
import pl.jwizard.jwc.audio.scheduler.AudioScheduleHandler
import pl.jwizard.jwc.audio.scheduler.QueueTrackScheduleHandler
import pl.jwizard.jwc.audio.scheduler.RadioStreamScheduleHandler
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.radio.RadioStation

/**
 * Manages the state of audio playback in a guild, providing scheduling for queued tracks and radio streams. This class
 * is responsible for switching between audio types and ensuring the correct scheduler is used for each type.
 *
 * @property guildMusicManager Manages the guild's audio player and track scheduler.
 * @property derivedContext The base command context, containing information about the current command execution.
 * @property derivedFuture The future response object to handle command responses asynchronously.
 * @author Miłosz Gilga
 */
class AudioStateManagerProvider(
	private val guildMusicManager: GuildMusicManager,
	private val derivedContext: GuildCommandContext,
	private val derivedFuture: TFutureResponse,
) {

	val audioScheduler get() = audioScheduleHandler
	val queueTrackScheduler get() = audioScheduler as QueueTrackScheduleHandler
	val radioStreamScheduler get() = audioScheduler as RadioStreamScheduleHandler

	/**
	 * The current type of audio content being played (either queued tracks or radio streams).
	 */
	private var audioType: AudioContentType? = null

	/**
	 * The handler responsible for managing the current audio playback, either for queued tracks or radio streams.
	 */
	private var audioScheduleHandler: AudioScheduleHandler = QueueTrackScheduleHandler(guildMusicManager)

	/**
	 * The context of the current command, used to maintain information related to the ongoing operation.
	 */
	var context = derivedContext
		private set

	/**
	 * The future response object to be updated with command responses asynchronously.
	 */
	var future = derivedFuture
		private set

	/**
	 * Switches the audio state to queued tracks and updates the scheduler to handle track queues.
	 *
	 * @param context The context of the command requesting the switch.
	 */
	fun setToQueueTrack(context: GuildCommandContext) {
		updateState(AudioContentType.QUEUE_TRACK, context)
		if (audioScheduleHandler !is QueueTrackScheduleHandler) {
			audioScheduleHandler = QueueTrackScheduleHandler(guildMusicManager)
		}
	}

	/**
	 * Switches the audio state to a radio stream and updates the scheduler to handle radio streaming.
	 *
	 * @param context The context of the command requesting the switch.
	 * @param radioStation Current selected [RadioStation] property.
	 */
	fun setToStream(context: GuildCommandContext, radioStation: RadioStation) {
		updateState(AudioContentType.STREAM, context)
		audioScheduleHandler = RadioStreamScheduleHandler(guildMusicManager, radioStation)
	}

	/**
	 * Updates the future response and context for the current operation.
	 *
	 * @param future The new future response object.
	 * @param context The context of the command.
	 */
	fun updateStateHandlers(future: TFutureResponse, context: GuildCommandContext) {
		this.future = future
		this.context = context
	}

	/**
	 * Clears the currently set audio type after ended audio stream.
	 */
	fun clearAudioType() {
		audioType = null
	}

	/**
	 * Checks if the current audio content type matches the specified type or if is not yet set.
	 *
	 * @param audioType The type of audio content to check against.
	 * @return True if the current audio type matches the specified type, false otherwise.
	 */
	fun isDeclaredAudioContentTypeOrNotYetSet(audioType: AudioContentType) =
		this.audioType == null || this.audioType == audioType

	/**
	 * Updates the internal state with the new audio content type and command context.
	 *
	 * @param audioType The new audio content type to switch to.
	 * @param context The command context to be associated with the new state.
	 */
	private fun updateState(audioType: AudioContentType, context: GuildCommandContext) {
		this.audioType = audioType
		this.context = context
	}
}
