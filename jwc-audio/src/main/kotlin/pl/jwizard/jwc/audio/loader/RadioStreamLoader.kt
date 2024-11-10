/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader

import dev.arbjerg.lavalink.client.player.*
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.radio.RadioStation

/**
 * Handles loading and managing radio streams for the music manager.
 *
 * @property musicManager The music manager responsible for managing the guild's audio playback.
 * @property radioStation Current selected [RadioStation] property.
 * @author Miłosz Gilga
 */
class RadioStreamLoader(
	private val musicManager: GuildMusicManager,
	private val radioStation: RadioStation,
) : AudioCompletableFutureLoader(musicManager) {

	override fun onCompletableTrackLoaded(result: TrackLoaded, future: TFutureResponse) = onStreamLoaded(result.track)

	override fun onCompletableSearchResultLoaded(result: SearchResult, future: TFutureResponse) =
		onStreamLoaded(result.tracks[0])

	override fun onCompletablePlaylistLoaded(result: PlaylistLoaded, future: TFutureResponse) =
		onStreamLoaded(result.tracks[0])

	/**
	 * Handles a failed loading attempt for a radio stream.
	 *
	 * @param result The result containing the details of the load failure.
	 * @return The details of the load failure.
	 */
	override fun onCompletableLoadFailed(result: LoadFailed) = AudioLoadFailedDetails.Builder()
		.setLogMessage(
			logMessage = "Unexpected error on load radio stream: %s. Cause: %s.",
			args = arrayOf(radioStation.textKey, result.exception.message)
		)
		.setI18nLocaleSource(
			i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_ON_LOAD_RADIO,
			args = mapOf("radioStation" to radioStationName)
		)
		.build()

	/**
	 * Handles cases where no tracks or playlists match the search for the radio stream.
	 *
	 * @return The details indicating no matches were found.
	 */
	override fun onCompletableNoMatches() = AudioLoadFailedDetails.Builder()
		.setLogMessage("Unexpected error on load radio stream: %s. Audio stuck.", radioStation.textKey)
		.setI18nLocaleSource(
			i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_ON_LOAD_RADIO,
			args = mapOf("radioStation" to radioStationName)
		)
		.build()

	/**
	 * Handles the error that occurs during the audio loading process.
	 *
	 * @param details The details of the error that occurred.
	 * @return A command response indicating the error.
	 */
	override fun onError(details: AudioLoadFailedDetails): CommandResponse {
		musicManager.state.audioScheduler.stopAndDestroy()
		musicManager.startLeavingWaiter()
		return super.onError(details)
	}

	/**
	 * Loads the specified track into the music manager's audio scheduler for playback.
	 *
	 * @param track The track to be loaded for the radio stream.
	 */
	private fun onStreamLoaded(track: Track) = musicManager.state.audioScheduler.loadContent(listOf(track))

	/**
	 * Retrieves the name of the radio station for localization purposes.
	 */
	private val radioStationName
		get() = musicManager.beans.i18nBean.t(radioStation, musicManager.state.context.guildLanguage)
}
