/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason
import pl.jwizard.jwac.node.AudioNode
import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwac.player.track.TrackException
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.mdList
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.radio.RadioStation
import pl.jwizard.jwl.util.logger

/**
 * Handles the scheduling and management of radio streams in a Discord bot context.
 *
 * This class is responsible for loading radio streams, managing their playback, and responding to events such as
 * starting, stopping, and error handling.
 *
 * @property guildMusicManager The [GuildMusicManager] instance used for managing audio playback.
 * @property radioStation Current selected [RadioStation] property.
 * @author Miłosz Gilga
 */
class RadioStreamScheduleHandler(
	private val guildMusicManager: GuildMusicManager,
	val radioStation: RadioStation,
) : AudioScheduleHandler(guildMusicManager) {

	companion object {
		private val log = logger<RadioStreamScheduleHandler>()
	}

	/**
	 * Loads the specified list of tracks into the queue. For radio streams, this method simply starts the first track in
	 * the provided list if it is not empty.
	 *
	 * @param tracks The list of [Track]s to be loaded into the queue. Typically only the first track is used for radio.
	 */
	override fun loadContent(tracks: List<Track>) {
		if (tracks.isNotEmpty()) {
			startTrack(tracks[0])
		}
	}

	/**
	 * Handles the event when an audio track starts playing. Sends a message to the context indicating that the radio
	 * station has started playing.
	 *
	 * @param track The [Track] that has started playing.
	 * @param audioNode The [AudioNode] on which the track is playing.
	 */
	override fun onAudioStart(track: Track, audioNode: AudioNode) {
		val state = guildMusicManager.state
		val context = state.context
		val i18nBean = guildMusicManager.bean.i18n

		val listElements = mapOf(
			I18nResponseSource.START_PLAYING_RADIO_STATION_FIRST_OPTION to mapOf("stopRadioStationCmd" to Command.RADIO_STOP),
			I18nResponseSource.START_PLAYING_RADIO_STATION_SECOND_OPTION to mapOf("radioStationInfoCmd" to Command.RADIO_INFO),
		)
		val parsedListElements = listElements.entries.joinToString("\n") { (i18nKey, i18nArgs) ->
			mdList(i18nBean.t(i18nKey, context.language, i18nArgs.mapValues { it.value.parseWithPrefix(context) }))
		}
		val (name, inputStream) = guildMusicManager.bean.radioStationThumbnailSupplier.getThumbnailResource(radioStation)
		val message = guildMusicManager.createEmbedBuilder()
			.setTitle(
				i18nLocaleSource = I18nResponseSource.START_PLAYING_RADIO_STATION,
				args = mapOf("radioStationName" to i18nBean.t(radioStation, context.language)),
			)
			.setDescription(parsedListElements)
			.setLocalArtwork(name)
			.setColor(JdaColor.PRIMARY)
			.build()

		log.jdaInfo(
			state.context,
			"Node: %s. Start playing radio station: %s from stream URL: %s.",
			audioNode.name,
			radioStation.textKey,
			radioStation.streamUrl,
		)
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addFiles(mapOf(name to inputStream))
			.build()
		state.future.complete(response)
	}

	/**
	 * Handles the event when an audio track ends.
	 *
	 * Sends a message to the context indicating that the radio station has stopped playing. Offers the option to start
	 * playing the radio station again.
	 *
	 * @param lastTrack The [Track] that just finished playing.
	 * @param audioNode The [AudioNode] on which the track was playing.
	 * @param endReason The reason for the track ending.
	 */
	override fun onAudioEnd(lastTrack: Track, audioNode: AudioNode, endReason: AudioTrackEndReason) {
		val state = guildMusicManager.state
		val context = state.context
		val (name, inputStream) = guildMusicManager.bean.radioStationThumbnailSupplier.getThumbnailResource(radioStation)

		val message = guildMusicManager.createEmbedBuilder()
			.setDescription(
				i18nLocaleSource = I18nResponseSource.STOP_PLAYING_RADIO_STATION,
				args = mapOf(
					"radioStationName" to guildMusicManager.bean.i18n.t(radioStation, context.language),
					"startRadioStationCmd" to Command.RADIO_PLAY.parseWithPrefix(context),
				),
			)
			.setColor(JdaColor.PRIMARY)
			.setLocalArtwork(name)
			.build()

		guildMusicManager.startLeavingWaiter()

		log.jdaInfo(context, "Node: %s. Stop playing radio station: %s.", audioNode.name, radioStation.textKey)
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addFiles(mapOf(name to inputStream))
			.build()
		state.future.complete(response)
	}

	/**
	 * Handles the event when an audio track gets stuck during playback. Calls the [onError] method to manage the error
	 * handling process.
	 *
	 * @param track The [Track] that is stuck.
	 * @param audioNode The [AudioNode] on which the track was playing.
	 */
	override fun onAudioStuck(track: Track, audioNode: AudioNode) = onError(audioNode, "Radio stuck.")

	/**
	 * Handles the event when an error occurs while playing an audio track. Logs the error and sends an appropriate
	 * message to the context.
	 *
	 * @param track The [Track] that encountered an error.
	 * @param audioNode The [AudioNode] on which the error occurred.
	 * @param exception The [TrackException] that contains the error details.
	 */
	override fun onAudioException(track: Track, audioNode: AudioNode, exception: TrackException) =
		onError(audioNode, exception.message)

	/**
	 * Handles errors that occur during radio stream playback. Logs the error and sends a message with details about
	 * the issue.
	 *
	 * @param audioNode The [AudioNode] on which the error occurred.
	 * @param logMessage A message explaining the cause of the error.
	 */
	private fun onError(audioNode: AudioNode, logMessage: String?) {
		val context = guildMusicManager.state.context
		val i18nBean = guildMusicManager.bean.i18n
		val tracker = guildMusicManager.bean.exceptionTrackerHandler

		guildMusicManager.state.audioScheduler.stopAndDestroy().subscribe()
		guildMusicManager.startLeavingWaiter()

		val i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_WHILE_STREAMING_RADIO
		val message = tracker.createTrackerMessage(
			i18nLocaleSource, context,
			args = mapOf("radioStation" to i18nBean.t(radioStation, context.language))
		)
		val link = tracker.createTrackerLink(i18nLocaleSource, context)
		log.jdaInfo(
			context,
			"Node: %s. Unexpected error while streaming radio: %s. Cause: %s.",
			audioNode.name,
			radioStation.textKey,
			logMessage
		)
		guildMusicManager.sendMessage(message, link)
	}
}
