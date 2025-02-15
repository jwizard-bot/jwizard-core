package pl.jwizard.jwc.audio.scheduler

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.gateway.player.track.TrackEndReason
import pl.jwizard.jwc.audio.gateway.player.track.TrackException
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.audio.scheduler.repeat.AudioTrackRepeat
import pl.jwizard.jwc.audio.scheduler.repeat.CountOfRepeats
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.I18nLocaleSource
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.util.logger
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicBoolean

class QueueTrackScheduleHandler(
	private val guildMusicManager: GuildMusicManager,
) : AudioScheduleHandler(guildMusicManager) {
	companion object {
		private val log = logger<QueueTrackScheduleHandler>()
	}

	val queue = AudioTrackQueue()
	val audioRepeat = AudioTrackRepeat()

	private val countOfRepeats = CountOfRepeats()
	private val nextTrackInfoMessage = AtomicBoolean(true)

	override fun loadContent(tracks: List<Track>) {
		if (tracks.isEmpty()) {
			return
		}
		val noTrackPlaying = guildMusicManager.cachedPlayer?.track == null
		if (tracks.size == 1) {
			val track = tracks[0]
			if (noTrackPlaying) {
				startTrack(track)
				return
			}
			queue.offer(track)
			return
		}
		queue.addAll(tracks)
		if (noTrackPlaying) {
			startTrack(queue.poll())
		}
	}

	override fun stopAndDestroy(): Mono<*> {
		queue.clear()
		audioRepeat.clear()
		countOfRepeats.clear()
		nextTrackInfoMessage.set(true)
		return super.stopAndDestroy()
	}

	fun updateCountOfRepeats(count: Int) {
		countOfRepeats.set(count)
		if (count > 0) {
			nextTrackInfoMessage.set(false)
		}
	}

	override fun onAudioStart(track: Track, audioNode: AudioNode) {
		val context = guildMusicManager.state.context
		if (guildMusicManager.cachedPlayer?.paused == true) {
			val message = createTrackStartMessage(
				track, I18nResponseSource.ON_TRACK_START_ON_PAUSED,
				"resumeCmd" to Command.RESUME.parseWithPrefix(context),
			)
			log.jdaInfo(
				context,
				"Node: %s. Start playing audio track: %s when audio player is paused.",
				audioNode.name,
				track.qualifier
			)
			guildMusicManager.sendMessage(message)
		} else {
			val message = createTrackStartMessage(track, I18nResponseSource.ON_TRACK_START)
			if (nextTrackInfoMessage.get()) {
				log.jdaInfo(
					context,
					"Node: %s. Start playing audio track: %s.",
					audioNode.name,
					track.qualifier,
				)
				guildMusicManager.sendMessage(message)
			}
		}
	}

	override fun onAudioEnd(lastTrack: Track, audioNode: AudioNode, endReason: TrackEndReason) {
		val context = guildMusicManager.state.context
		if (audioRepeat.trackRepeat) {
			nextTrackInfoMessage.set(false) // disable for prevent spamming
			startTrack(lastTrack.makeClone())
			return
		}
		if (audioRepeat.playlistRepeat) {
			nextTrack()
			queue.add(lastTrack.makeClone())
			return
		}
		if (countOfRepeats.current > 0) { // repeat selected track multiple times
			startTrack(lastTrack.makeClone())
			nextTrackInfoMessage.set(false) // disable for prevent spamming
			countOfRepeats.decrease()
			val trackRepeatMessage = guildMusicManager.createEmbedBuilder()
				.setDescription(
					i18nLocaleSource = I18nResponseSource.MULTIPLE_REPEATING_TRACK_INFO,
					args = mapOf(
						"currentRepeat" to countOfRepeats.currentRepeat,
						"track" to lastTrack.mdTitleLink,
						"elapsedRepeats" to countOfRepeats.current,
					),
				)
				.setArtwork(lastTrack.thumbnailUrl)
				.setColor(JdaColor.PRIMARY)
				.build()
			log.jdaInfo(
				context,
				"Node: %s. Repeat: %d times of track: %s from elapsed: %d repeats.",
				audioNode.name,
				countOfRepeats.currentRepeat,
				lastTrack.qualifier,
				countOfRepeats.current
			)
			guildMusicManager.sendMessage(trackRepeatMessage)
			return
		}
		if (queue.isEmpty() && endReason != TrackEndReason.REPLACED) {
			val connectionInterrupted = guildMusicManager.cachedPlayer?.state == null
			nextTrackInfoMessage.set(true)

			val endQueueMessage = guildMusicManager.createEmbedBuilder()
				.setDescription(I18nResponseSource.ON_END_PLAYBACK_QUEUE)
				.setColor(JdaColor.PRIMARY)
				.build()

			if (connectionInterrupted) {
				queue.clear()
				audioRepeat.clear()
				countOfRepeats.clear()
			} else {
				guildMusicManager.startLeavingWaiter()
			}
			guildMusicManager.sendMessage(endQueueMessage)
			return
		}
		if (endReason.mayStartNext || (endReason == TrackEndReason.STOPPED && queue.isNotEmpty())) {
			nextTrackInfoMessage.set(true)
			nextTrack()
		}
	}

	override fun onAudioStuck(
		track: Track,
		audioNode: AudioNode,
	) = onError(track, audioNode, I18nExceptionSource.ISSUE_WHILE_PLAYING_TRACK, "Track stuck.")

	override fun onAudioException(track: Track, audioNode: AudioNode, exception: TrackException) {
		val parsedMessage = exception.message?.replace("\n", "")

		val specifiedException = PlayerFriendlyExceptionMapper.entries
			.filter { it.stringPattern != null }
			.find { parsedMessage?.contains(it.stringPattern!!) == true }
			?: PlayerFriendlyExceptionMapper.GENERAL

		return onError(track, audioNode, specifiedException.i18nLocaleSource, parsedMessage)
	}

	private fun onError(
		track: Track,
		audioNode: AudioNode,
		i18nSource: I18nExceptionSource,
		causeMessage: String?,
	) {
		val context = guildMusicManager.state.context
		val tracker = guildMusicManager.bean.exceptionTrackerHandler

		val message = tracker.createTrackerMessage(
			i18nSource,
			context,
			args = mapOf("audioTrack" to track.mdTitleLink)
		)
		val trackerLink = tracker.createTrackerLink(i18nSource, context)

		if (queue.isEmpty() && guildMusicManager.cachedPlayer?.track == null) {
			guildMusicManager.startLeavingWaiter()
		}
		log.jdaError(
			context,
			"Node: %s. Unexpected issue while playing track: %s. Cause: %s.",
			audioNode.name,
			track.qualifier,
			causeMessage
		)
		guildMusicManager.sendMessage(message, trackerLink)
	}

	private fun createTrackStartMessage(
		track: Track,
		i18nSource: I18nLocaleSource,
		vararg args: Pair<String, Any?>,
	): MessageEmbed {
		val mapArgs = mutableMapOf<String, Any?>()
		mapArgs += "track" to track.mdTitleLink
		args.forEach { mapArgs += it }
		return guildMusicManager.createEmbedBuilder()
			.setDescription(i18nSource, mapArgs)
			.setArtwork(track.thumbnailUrl)
			.setColor(JdaColor.PRIMARY)
			.build()
	}

	private fun nextTrack() = queue.poll()?.let { startTrack(it) }
}
