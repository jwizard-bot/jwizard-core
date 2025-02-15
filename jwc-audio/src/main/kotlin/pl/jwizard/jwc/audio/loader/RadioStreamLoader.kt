package pl.jwizard.jwc.audio.loader

import pl.jwizard.jwc.audio.gateway.event.onload.*
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.radio.RadioStation

class RadioStreamLoader(
	private val guildMusicManager: GuildMusicManager,
	private val radioStation: RadioStation,
) : AudioCompletableFutureLoader(guildMusicManager) {

	private val radioStationName
		get() = guildMusicManager.bean.i18n.t(radioStation, guildMusicManager.state.context.language)

	override fun onCompletableTrackLoaded(
		result: KTrackLoadedEvent,
		future: TFutureResponse,
	) = onStreamLoaded(result.track)

	override fun onCompletableSearchResultLoaded(
		result: KSearchResultEvent,
		future: TFutureResponse,
	) =
		onStreamLoaded(result.tracks[0])

	override fun onCompletablePlaylistLoaded(
		result: KPlaylistLoadedEvent,
		future: TFutureResponse,
	) = onStreamLoaded(result.tracks[0])

	override fun onCompletableLoadFailed(result: KLoadFailedEvent) = AudioLoadFailedDetails.Builder()
		.setLogMessage(
			logMessage = "Unexpected error on load radio stream: %s. Cause: %s.",
			args = arrayOf(radioStation.textKey, result.exception.message)
		)
		.setI18nLocaleSource(
			i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_ON_LOAD_RADIO,
			args = mapOf("radioStation" to radioStationName)
		)
		.build()

	override fun onCompletableNoMatches(result: KNoMatchesEvent) = AudioLoadFailedDetails.Builder()
		.setLogMessage("Unexpected error on load radio stream: %s. Audio stuck.", radioStation.textKey)
		.setI18nLocaleSource(
			i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_ON_LOAD_RADIO,
			args = mapOf("radioStation" to radioStationName)
		)
		.build()

	override fun onError(details: AudioLoadFailedDetails) {
		guildMusicManager.state.audioScheduler.stopAndDestroy().subscribe()
		guildMusicManager.startLeavingWaiter()
		super.onError(details)
	}

	private fun onStreamLoaded(
		track: Track,
	) = guildMusicManager.state.audioScheduler.loadContent(listOf(track))
}
