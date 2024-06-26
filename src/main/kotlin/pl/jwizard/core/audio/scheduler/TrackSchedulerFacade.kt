/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.scheduler

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.DefferedEmbed
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.exception.I18nExceptionLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.Formatter
import java.util.concurrent.TimeUnit

class TrackSchedulerFacade(
	private val audioScheduler: AudioScheduler
) : AbstractLoggingBean(TrackSchedulerFacade::class), AudioSchedulerContract {

	private val audioPlayer = audioScheduler.audioPlayer
	private val actions = audioScheduler.schedulerActions
	private val botConfiguration = audioScheduler.botConfiguration

	override fun onPause() {
		val audioTrack = audioScheduler.audioPlayer.playingTrack
		if (audioPlayer.playingTrack != null && !actions.onClearing) {
			actions.pausedTrack = audioTrack
			jdaLog.info(audioScheduler.event, "Audio track: ${audioTrack.info.title} was resumed")
		}
	}

	override fun onResume() {
		val audioTrack = audioScheduler.audioPlayer.playingTrack
		if (actions.pausedTrack != null && !actions.onClearing) {
			actions.pausedTrack = null
			jdaLog.info(audioScheduler.event, "Audio track: ${audioTrack.info.title} was paused")
		}
	}

	override fun onStart() {
		actions.threadsCountToLeave?.cancel(false)
		if (actions.onClearing) {
			return
		}
		val event = audioScheduler.event
		val audioTrackInfo = ExtendedAudioTrackInfo(audioPlayer.playingTrack)
		if (audioPlayer.isPaused) {
			val messageEmbed = CustomEmbedBuilder(botConfiguration, event).buildTrackMessage(
				placeholder = I18nResLocale.ON_TRACK_START_ON_PAUSED,
				params = mapOf(
					"track" to Formatter.createRichTrackTitle(audioTrackInfo),
					"resumeCmd" to BotCommand.RESUME.parseWithPrefix(event),
				),
				thumbnailUrl = audioTrackInfo.artworkUrl
			)
			audioScheduler.event.instantlySendEmbedMessage(messageEmbed, legacyTransport = true)
			jdaLog.info(event, "Starting playing audio track: ${audioTrackInfo.title} when audio player is paused")
		} else {
			val messageEmbed = CustomEmbedBuilder(botConfiguration, event).buildTrackMessage(
				placeholder = I18nResLocale.ON_TRACK_START,
				params = mapOf(
					"track" to Formatter.createRichTrackTitle(audioTrackInfo),
				),
				thumbnailUrl = audioTrackInfo.artworkUrl
			)
			jdaLog.info(event, "Starting playing audio track: ${audioTrackInfo.title}")
			if (actions.nextTrackInfoDisabled && !actions.infiniteRepeating) {
				actions.nextTrackInfoDisabled = false
			}
			if (!actions.nextTrackInfoDisabled) {
				audioScheduler.event.instantlySendEmbedMessage(
					messageEmbed,
					delay = DefferedEmbed(if (event.invokedBySender) 0 else 1, TimeUnit.SECONDS),
					legacyTransport = !event.invokedBySender
				)
			}
			event.invokedBySender = false // reset invoking hook, start sending bot messages via legacy transport
		}
		actions.nextTrackInfoDisabled = actions.infiniteRepeating
	}

	override fun onEnd(track: AudioTrack, endReason: AudioTrackEndReason) {
		val event = audioScheduler.event
		if (actions.onClearing) {
			return
		}
		val isNoneRepeating = !actions.infiniteRepeating && actions.countOfRepeats == 0
		if (audioPlayer.playingTrack == null && actions.trackQueue.isEmpty() && isNoneRepeating) {
			actions.nextTrackInfoDisabled = false

			val messageEmbed = CustomEmbedBuilder(botConfiguration, event).buildBaseMessage(
				placeholder = I18nResLocale.ON_END_PLAYBACK_QUEUE,
			)
			event.instantlySendEmbedMessage(messageEmbed, legacyTransport = true)

			jdaLog.info(event, "End of playing queue tracks")
			if (!audioScheduler.lockedGuilds.contains(event.guildId) || actions.radioStationDto == null) {
				actions.leaveAndSendMessageAfterInactivity()
			}
			return
		}
		if (actions.infiniteRepeating) {
			audioPlayer.startTrack(track.makeClone(), false)
			return
		}
		if (actions.infinitePlaylistRepeating) {
			actions.trackQueue.add(track.makeClone())
			if (endReason.mayStartNext) {
				actions.nextTrack()
			}
			return
		}
		if (actions.countOfRepeats > 0) {
			val currentRepeat = actions.currentRepeat()

			audioPlayer.startTrack(track.makeClone(), false)
			actions.nextTrackInfoDisabled = true
			actions.countOfRepeats -= 1

			val messageEmbed = CustomEmbedBuilder(botConfiguration, event).buildBaseMessage(
				placeholder = I18nResLocale.MULTIPLE_REPEATING_TRACK_INFO,
				params = mapOf(
					"currentRepeat" to currentRepeat,
					"track" to Formatter.createRichTrackTitle(track),
					"elapsedRepeats" to actions.countOfRepeats,
				)
			)
			event.instantlySendEmbedMessage(messageEmbed, legacyTransport = true)
			jdaLog.info(
				event, "Repeat $currentRepeat times of track: ${track.info.title} from elapsed " +
					"${actions.countOfRepeats} repeats"
			)
			return
		}
		if (endReason.mayStartNext) {
			actions.nextTrack()
		}
	}

	override fun onException(track: AudioTrack, ex: FriendlyException) {
		val event = audioScheduler.event
		val messageEmbed = CustomEmbedBuilder(botConfiguration, event).buildErrorMessage(
			placeholder = I18nExceptionLocale.ISSUE_WHILE_PLAYING_TRACK,
		)
		event.instantlySendEmbedMessage(messageEmbed, legacyTransport = true)
		// clear queue after unexpected exception
		val actions = audioScheduler.schedulerActions
		if (actions.trackQueue.isEmpty() && audioPlayer.playingTrack == null) {
			actions.leaveAndSendMessageAfterInactivity()
		}
		jdaLog.error(event, "Unexpected issue while playing track: ${track.info.title}. Cause: ${ex.message}")
	}
}
