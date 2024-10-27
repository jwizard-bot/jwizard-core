/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader

import dev.arbjerg.lavalink.client.player.*
import pl.jwizard.jwc.audio.RadioStationDetails
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.i18n.source.I18nDynamicMod

/**
 * Handles loading and managing radio streams for the music manager.
 *
 * @property musicManager The music manager responsible for managing the guild's audio playback.
 * @property radioStationDetails The details of the radio station to be loaded.
 * @author Miłosz Gilga
 */
class RadioStreamLoader(
	private val musicManager: GuildMusicManager,
	private val radioStationDetails: RadioStationDetails,
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
			args = arrayOf(radioStationDetails.name, result.exception.message)
		)
		.setI18nLocaleSource(
			i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_ON_LOAD_RADIO,
			args = mapOf("radioStation" to getRadioStationName(radioStationDetails))
		)
		.build()

	/**
	 * Handles cases where no tracks or playlists match the search for the radio stream.
	 *
	 * @return The details indicating no matches were found.
	 */
	override fun onCompletableNoMatches() = AudioLoadFailedDetails.Builder()
		.setLogMessage("Unexpected error on load radio stream: %s. Audio stuck.", radioStationDetails.name)
		.setI18nLocaleSource(
			i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_ON_LOAD_RADIO,
			args = mapOf("radioStation" to getRadioStationName(radioStationDetails))
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
	 *
	 * @param details The details of the radio station.
	 * @return The localized name of the radio station.
	 */
	private fun getRadioStationName(details: RadioStationDetails) = musicManager.beans.i18nBean.tRaw(
		i18nDynamicMod = I18nDynamicMod.ARG_OPTION_MOD,
		args = arrayOf("radio", details.name),
		lang = musicManager.state.context.guildLanguage,
	)
}
