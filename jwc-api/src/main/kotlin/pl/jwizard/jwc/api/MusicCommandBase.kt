/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api

import dev.arbjerg.lavalink.client.player.Track
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.ChannelType
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.i18n.I18nLocaleSource
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.PercentageIndicatorBar
import pl.jwizard.jwc.core.util.ext.duration
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.thumbnailUrl
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwc.exception.audio.ActiveAudioPlayingNotFoundException
import pl.jwizard.jwc.exception.audio.PlayerNotPausedException
import pl.jwizard.jwc.exception.track.TrackQueueIsEmptyException
import java.time.Duration

/**
 * Base class for commands related to music playback.
 *
 * This class extends the AudioCommandBase and provides a foundation for commands that involve music playback
 * functionalities. It includes checks for the audio player state and user permissions related to music commands.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
abstract class MusicCommandBase(commandEnvironment: CommandEnvironmentBean) : AudioCommandBase(commandEnvironment) {

	/**
	 * Executes the music command after verifying the audio player state and user permissions.
	 *
	 * This method checks whether the audio player is in an appropriate state for the command to be executed. It handles
	 * scenarios such as:
	 * - Ensuring the bot is in a voice channel.
	 * - Checking if the player is actively playing or paused, based on the command's requirements.
	 * - Validating that the track queue is not empty if required.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager handling the audio playback.
	 * @param response The future response object used to send the result of the command execution.
	 * @throws ActiveAudioPlayingNotFoundException If the player is not playing any audio.
	 * @throws PlayerNotPausedException If the command requires the player to be paused, but it is not.
	 * @throws TrackQueueIsEmptyException If the command requires tracks in the queue but none are present.
	 */
	final override fun executeAudio(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		// TODO: check, if player is radio station player

		val player = manager.cachedPlayer
		val isActive = context.selfMember?.voiceState?.channel?.type == ChannelType.VOICE
		val inPlayingMode = isActive && player?.track != null

		if (shouldPlayingMode && (!inPlayingMode || player?.paused == true) && !shouldPaused) {
			throw ActiveAudioPlayingNotFoundException(context)
		}
		if (!shouldIdleMode) {
			if (shouldPaused && player?.paused == false) {
				throw PlayerNotPausedException(context)
			}
			val userVoiceState = checkUserVoiceState(context)
			if (userIsWithBotOnAudioChannel(userVoiceState, context)) {
				joinAndOpenAudioConnection(context)
			}
		}
		if (queueShouldNotBeEmpty && manager.audioScheduler.queue.queueSize() == 0) {
			throw TrackQueueIsEmptyException(context)
		}
		executeMusic(context, manager, response)
	}

	/**
	 * Creates a detailed message about the currently playing track.
	 *
	 * This method generates an embedded message that includes details such as:
	 * - The track name and artist.
	 * - The elapsed time and total duration of the track.
	 * - The user who added the track to the queue (if available).
	 * - A visual progress bar indicating the percentage of the track that has been played.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @param i18nTitle The localized title text for the track (ex. "Now Playing").
	 * @param i18nPosition The localized text indicating the current position in the track.
	 * @param track The track that is currently being played.
	 * @return A MessageEmbed containing the detailed track information to be sent as a response.
	 */
	protected fun createDetailedTrackMessage(
		context: CommandContext,
		manager: MusicManager,
		i18nTitle: I18nLocaleSource,
		i18nPosition: I18nLocaleSource,
		track: Track,
	): MessageEmbed {
		val elapsedTime = manager.cachedPlayer?.position ?: 0
		val audioSender = manager.getAudioSenderId(track)?.let { context.guild?.getMemberById(it) }
		val percentageIndicatorBar = PercentageIndicatorBar(
			start = Duration.ofMillis(elapsedTime),
			total = Duration.ofMillis(track.duration),
		)
		val messageBuilder = createEmbedMessage(context)
			.setTitle(i18nTitle)
			.setKeyValueField(I18nAudioSource.TRACK_NAME, track.mdTitleLink)
		audioSender?.let {
			messageBuilder.setSpace()
			messageBuilder.setKeyValueField(I18nAudioSource.TRACK_ADDED_BY, it.user.name)
		}
		return messageBuilder.setValueField(percentageIndicatorBar.generateBar(), inline = false)
			.setKeyValueField(i18nPosition, "${millisToDTF(elapsedTime)} / ${millisToDTF(track.duration)}")
			.setSpace()
			.setKeyValueField(I18nAudioSource.CURRENT_TRACK_LEFT_TO_NEXT, millisToDTF(track.duration - elapsedTime))
			.setArtwork(track.thumbnailUrl)
			.setColor(JdaColor.PRIMARY)
			.build()
	}

	/**
	 * Indicates if the command should only be invoked when the bot is actively playing audio.
	 */
	protected open val shouldPlayingMode = false

	/**
	 * Indicates if the command can be invoked even when the bot is idle (not playing audio).
	 */
	protected open val shouldIdleMode = false

	/**
	 * Indicates if the command should only be invoked when the currently playing audio is paused.
	 */
	protected open val shouldPaused = false

	/**
	 * Indicates if the command requires the track queue to not be empty.
	 */
	protected open val queueShouldNotBeEmpty = false

	/**
	 * Executes the specific music command functionality.
	 *
	 * This method must be implemented by subclasses to define the specific behavior of the music command. It is called
	 * only after the necessary checks for the player state and user permissions have passed.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager handling the audio playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	protected abstract fun executeMusic(context: CommandContext, manager: MusicManager, response: TFutureResponse)
}
