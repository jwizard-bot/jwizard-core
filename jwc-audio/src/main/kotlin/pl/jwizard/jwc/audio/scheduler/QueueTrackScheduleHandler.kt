/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler

import dev.arbjerg.lavalink.client.LavalinkNode
import dev.arbjerg.lavalink.client.player.Track
import dev.arbjerg.lavalink.client.player.TrackException
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.audio.scheduler.repeat.AudioTrackRepeat
import pl.jwizard.jwc.audio.scheduler.repeat.CountOfRepeats
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.ext.thumbnailUrl
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.I18nLocaleSource
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.util.logger
import reactor.core.Disposable
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Handles the scheduling and management of audio tracks in a queue.
 *
 * This class extends [AudioScheduleHandler], providing functionality for loading tracks, managing playback, and
 * handling events related to audio track playback.
 *
 * @property guildMusicManager The [GuildMusicManager] instance used for managing audio tracks and interactions.
 * @author Miłosz Gilga
 */
class QueueTrackScheduleHandler(
	private val guildMusicManager: GuildMusicManager,
) : AudioScheduleHandler(guildMusicManager) {

	companion object {
		private val log = logger<QueueTrackScheduleHandler>()
	}

	val queue = AudioTrackQueue(guildMusicManager)
	val audioRepeat = AudioTrackRepeat()

	/**
	 * The current count of repeats for the selected track.
	 */
	private val countOfRepeats = CountOfRepeats()

	/**
	 * Flag indicating whether to send the next track info message.
	 */
	private val nextTrackInfoMessage = AtomicBoolean(true)

	/**
	 * Loads the specified list of tracks into the queue.
	 *
	 * If the list is empty, the method does nothing. If a single track is provided and no track is currently playing,
	 * that track will be started immediately. If multiple tracks are provided, they are added to the queue.
	 *
	 * @param tracks The list of [Track]s to be loaded into the queue.
	 */
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

	/**
	 * Stops the current playback, clears the queue, and resets all repeat settings. This method also resets the flag for
	 * the next track info message.
	 *
	 * @return The [Mono] as an asynchronous response.
	 */
	override fun stopAndDestroy(): Mono<*> {
		queue.clear()
		audioRepeat.clear()
		countOfRepeats.clear()
		nextTrackInfoMessage.set(true)
		return super.stopAndDestroy()
	}

	/**
	 * Updates the count of repeats for the currently playing track. If the count is greater than zero, the flag for
	 * sending the next track info message is set to false.
	 *
	 * @param count The number of repeats to be set.
	 */
	fun updateCountOfRepeats(count: Int) {
		countOfRepeats.set(count)
		if (count > 0) {
			nextTrackInfoMessage.set(false)
		}
	}

	/**
	 * Handles the event when an audio track starts playing.
	 *
	 * Sends a message to the context indicating that the track has started. If the player was paused, it provides
	 * a command to resume playback.
	 *
	 * @param track The [Track] that started playing.
	 * @param node The [LavalinkNode] on which the track is playing.
	 */
	override fun onAudioStart(track: Track, node: LavalinkNode) {
		val context = guildMusicManager.state.context
		if (guildMusicManager.cachedPlayer?.paused == true) {
			val message = createTrackStartMessage(
				track, I18nResponseSource.ON_TRACK_START_ON_PAUSED,
				"resumeCmd" to Command.RESUME.parseWithPrefix(context.prefix)
			)
			log.jdaInfo(
				context,
				"Node: %s. Start playing audio track: %s when audio player is paused.",
				node.name,
				track.qualifier
			)
			guildMusicManager.sendMessage(message)
		} else {
			val message = createTrackStartMessage(track, I18nResponseSource.ON_TRACK_START)
			if (nextTrackInfoMessage.get()) {
				log.jdaInfo(context, "Node: %s. Start playing audio track: %s.", node.name, track.qualifier)
				guildMusicManager.sendMessage(message)
			}
		}
	}

	/**
	 * Handles the event when an audio track ends.
	 *
	 * Depending on the repeat settings and the state of the queue, it may restart the track, add it back to the queue,
	 * or move to the next track. It also handles the situation where the queue is empty.
	 *
	 * @param lastTrack The [Track] that just finished playing.
	 * @param node The [LavalinkNode] on which the track was playing.
	 * @param endReason The reason for the track ending.
	 */
	override fun onAudioEnd(lastTrack: Track, node: LavalinkNode, endReason: AudioTrackEndReason) {
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
				node.name,
				countOfRepeats.currentRepeat,
				lastTrack.qualifier,
				countOfRepeats.current
			)
			guildMusicManager.sendMessage(trackRepeatMessage)
			return
		}
		if (queue.isEmpty() && endReason != AudioTrackEndReason.REPLACED) {
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
		if (endReason.mayStartNext || (endReason == AudioTrackEndReason.STOPPED && queue.isNotEmpty())) {
			nextTrackInfoMessage.set(true)
			nextTrack()
		}
	}

	/**
	 * Handles the event when an audio track gets stuck during playback. Calls the [onError] method to manage the error
	 * handling process.
	 *
	 * @param track The [Track] that is stuck.
	 * @param node The [LavalinkNode] on which the track was playing.
	 */
	override fun onAudioStuck(track: Track, node: LavalinkNode) = onError(track, node, "Track stuck.")

	/**
	 * Handles the event when an error occurs while playing an audio track. Logs the error and sends an appropriate
	 * message to the context.
	 *
	 * @param track The [Track] that encountered an error.
	 * @param node The [LavalinkNode] on which the error occurred.
	 * @param exception The [TrackException] that contains the error details.
	 */
	override fun onAudioException(track: Track, node: LavalinkNode, exception: TrackException) =
		onError(track, node, exception.message)

	/**
	 * Handles errors that occur during track playback. Logs the error and sends a message with details about the issue.
	 *
	 * @param track The [Track] that caused the error.
	 * @param node The [LavalinkNode] on which the error occurred.
	 * @param causeMessage A message explaining the cause of the error.
	 */
	private fun onError(track: Track, node: LavalinkNode, causeMessage: String?) {
		val context = guildMusicManager.state.context
		val tracker = guildMusicManager.bean.exceptionTrackerHandler

		val i18nSource = I18nExceptionSource.ISSUE_WHILE_PLAYING_TRACK
		val message = tracker.createTrackerMessage(i18nSource, context, args = mapOf("audioTrack" to track.mdTitleLink))
		val trackerLink = tracker.createTrackerLink(i18nSource, context)

		if (queue.isEmpty() && guildMusicManager.cachedPlayer?.track == null) {
			guildMusicManager.startLeavingWaiter()
		}
		log.jdaError(
			context,
			"Node: %s. Unexpected issue while playing track: %s. Cause: %s.",
			node.name,
			track.qualifier,
			causeMessage
		)
		guildMusicManager.sendMessage(message, trackerLink)
	}

	/**
	 * Creates a message to indicate that a track has started playing.
	 *
	 * @param track The [Track] that has started.
	 * @param i18nSource The source for internationalization of the message.
	 * @param args Additional arguments to be included in the message.
	 * @return A [MessageEmbed] containing the track start message.
	 */
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

	/**
	 * Starts the next track in the queue, if available.
	 *
	 * @return A [Disposable] that can be used to manage the subscription to this operation.
	 */
	private fun nextTrack() = queue.poll()?.let { startTrack(it) }
}
