/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.scheduler

import java.util.concurrent.TimeUnit
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.DefferedEmbed
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.exception.I18nExceptionLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.Formatter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason

class TrackSchedulerFacade(
	private val trackScheduler: TrackScheduler
) : AbstractLoggingBean(TrackSchedulerFacade::class), TrackSchedulerContract {

	private val audioPlayer = trackScheduler.audioPlayer
	private val actions = trackScheduler.schedulerActions
	private val botConfiguration = trackScheduler.botConfiguration

	override fun onPause() {
		val audioTrack = trackScheduler.audioPlayer.playingTrack
		if (audioPlayer.playingTrack != null && !actions.onClearing) {
			actions.pausedTrack = audioTrack
			jdaLog.info(trackScheduler.event, "Audio track: ${audioTrack.info.title} was resumed")
		}
	}

	override fun onResume() {
		val audioTrack = trackScheduler.audioPlayer.playingTrack
		if (actions.pausedTrack != null && !actions.onClearing) {
			actions.pausedTrack = null
			jdaLog.info(trackScheduler.event, "Audio track: ${audioTrack.info.title} was paused")
		}
	}

	override fun onStart() {
		actions.threadsCountToLeave?.cancel(false)
		if (actions.nextTrackInfoDisabled || actions.onClearing) {
			return
		}
		val event = trackScheduler.event
		val audioTrackInfo = ExtendedAudioTrackInfo(audioPlayer.playingTrack)
		if (audioPlayer.isPaused) {
			val messageEmbed = CustomEmbedBuilder(event, botConfiguration).buildTrackMessage(
				placeholder = I18nResLocale.ON_TRACK_START_ON_PAUSED,
				params = mapOf(
					"track" to Formatter.createRichTrackTitle(audioTrackInfo),
					"resumeCmd" to BotCommand.RESUME.parseWithPrefix(botConfiguration, event),
				),
				thumbnailUrl = audioTrackInfo.thumbnailUrl
			)
			jdaLog.info(event, "Staring playing audio track: ${audioTrackInfo.title} when audio player is paused")
			trackScheduler.event.instantlySendEmbedMessage(
				messageEmbed,
				DefferedEmbed(1, TimeUnit.SECONDS),
				legacyTransport = true
			)
		} else {
			jdaLog.info(event, "Staring playing audio track: ${audioTrackInfo.title}")
		}
		if (actions.infiniteRepeating) {
			actions.nextTrackInfoDisabled = true
		}
	}

	override fun onEnd(track: AudioTrack, endReason: AudioTrackEndReason) {
		val event = trackScheduler.event
		if (actions.onClearing) {
			return
		}
		val isNoneRepeating = !actions.infiniteRepeating && actions.countOfRepeats == 0
		if (audioPlayer.playingTrack == null && actions.trackQueue.isEmpty() && isNoneRepeating) {
			actions.nextTrackInfoDisabled = false

			val messageEmbed = CustomEmbedBuilder(event, botConfiguration).buildBaseMessage(
				placeholder = I18nResLocale.ON_END_PLAYBACK_QUEUE,
			)
			event.instantlySendEmbedMessage(messageEmbed, legacyTransport = true)

			jdaLog.info(event, "End of playing queue tracks")
			if (!trackScheduler.lockedGuilds.contains(event.guildId)) {
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

			val messageEmbed = CustomEmbedBuilder(event, botConfiguration).buildBaseMessage(
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
		val event = trackScheduler.event
		val messageEmbed = CustomEmbedBuilder(event, botConfiguration).buildErrorMessage(
			placeholder = I18nExceptionLocale.ISSUE_WHILE_PLAYING_TRACK,
		)
		event.instantlySendEmbedMessage(messageEmbed, legacyTransport = true)
		// clear queue after unexpected exception
		val actions = trackScheduler.schedulerActions
		if (actions.trackQueue.isEmpty() && audioPlayer.playingTrack == null) {
			actions.leaveAndSendMessageAfterInactivity()
		}
		jdaLog.error(event, "Unexpected issue while playing track: ${track.info.title}. Cause: ${ex.message}")
	}
}
