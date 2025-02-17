package pl.jwizard.jwc.audio.scheduler

import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.gateway.player.track.TrackEndReason
import pl.jwizard.jwc.audio.gateway.player.track.TrackException
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

class RadioStreamScheduleHandler(
	private val guildMusicManager: GuildMusicManager,
	val radioStation: RadioStation,
) : AudioScheduleHandler(guildMusicManager) {
	companion object {
		private val log = logger<RadioStreamScheduleHandler>()
	}

	override fun loadContent(tracks: List<Track>) {
		if (tracks.isNotEmpty()) {
			startTrack(tracks[0])
		}
	}

	override fun onAudioStart(track: Track, audioNode: AudioNode) {
		val state = guildMusicManager.state
		val context = state.context
		val i18n = guildMusicManager.bean.i18n

		val listElements = mapOf(
			I18nResponseSource.START_PLAYING_RADIO_STATION_FIRST_OPTION to mapOf(
				"stopRadioStationCmd" to Command.RADIO_STOP
			),
			I18nResponseSource.START_PLAYING_RADIO_STATION_SECOND_OPTION to mapOf(
				"radioStationInfoCmd" to Command.RADIO_INFO
			),
		)
		val parsedListElements = listElements.entries.joinToString("\n") { (i18nKey, i18nArgs) ->
			mdList(
				i18n.t(
					i18nKey,
					context.language,
					i18nArgs.mapValues { it.value.parseWithPrefix(context) }),
			)
		}
		val message = guildMusicManager.createEmbedBuilder()
			.setTitle(
				i18nLocaleSource = I18nResponseSource.START_PLAYING_RADIO_STATION,
				args = mapOf("radioStationName" to i18n.t(radioStation, context.language)),
			)
			.setDescription(parsedListElements)
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
			.build()
		state.future.complete(response)
	}

	override fun onAudioEnd(lastTrack: Track, audioNode: AudioNode, endReason: TrackEndReason) {
		val state = guildMusicManager.state
		val context = state.context

		val message = guildMusicManager.createEmbedBuilder()
			.setDescription(
				i18nLocaleSource = I18nResponseSource.STOP_PLAYING_RADIO_STATION,
				args = mapOf(
					"radioStationName" to guildMusicManager.bean.i18n.t(radioStation, context.language),
					"startRadioStationCmd" to Command.RADIO_PLAY.parseWithPrefix(context),
				),
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		guildMusicManager.startLeavingWaiter()

		log.jdaInfo(
			context,
			"Node: %s. Stop playing radio station: %s.",
			audioNode.name,
			radioStation.textKey,
		)
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()
		state.future.complete(response)
	}

	override fun onAudioStuck(
		track: Track,
		audioNode: AudioNode,
	) = onError(audioNode, "Radio stuck.")

	override fun onAudioException(
		track: Track,
		audioNode: AudioNode,
		exception: TrackException,
	) = onError(audioNode, exception.message)

	private fun onError(audioNode: AudioNode, logMessage: String?) {
		val context = guildMusicManager.state.context
		val i18n = guildMusicManager.bean.i18n
		val tracker = guildMusicManager.bean.exceptionTrackerHandler

		guildMusicManager.state.audioScheduler.stopAndDestroy().subscribe()
		guildMusicManager.startLeavingWaiter()

		val i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_WHILE_STREAMING_RADIO
		val message = tracker.createTrackerMessage(
			i18nLocaleSource, context,
			args = mapOf("radioStation" to i18n.t(radioStation, context.language))
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
