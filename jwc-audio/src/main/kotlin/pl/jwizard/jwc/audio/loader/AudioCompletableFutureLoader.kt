/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader

import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler
import dev.arbjerg.lavalink.client.player.LoadFailed
import dev.arbjerg.lavalink.client.player.PlaylistLoaded
import dev.arbjerg.lavalink.client.player.SearchResult
import dev.arbjerg.lavalink.client.player.TrackLoaded
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwc.core.util.logger

/**
 * Abstract base class for handling audio loading results and managing audio loading processes. This class extends
 * [AbstractAudioLoadResultHandler] to handle various audio load results and complete futures associated with audio
 * commands.
 *
 * @property musicManager The music manager instance used for managing audio playback in the guild.
 * @author Miłosz Gilga
 */
abstract class AudioCompletableFutureLoader(
	private val musicManager: GuildMusicManager
) : AbstractAudioLoadResultHandler() {

	companion object {
		private val log = logger<AudioCompletableFutureLoader>()
	}

	/**
	 * Called when a track is successfully loaded. Stops the inactivity timer and handles the loaded track.
	 *
	 * @param result The loaded track result.
	 */
	final override fun ontrackLoaded(result: TrackLoaded) {
		musicManager.stopLeavingWaiter()
		onCompletableTrackLoaded(result, musicManager.state.future)
	}

	/**
	 * Called when search results are successfully loaded. Stops the inactivity timer and handles the search results.
	 *
	 * @param result The loaded search results.
	 */
	final override fun onSearchResultLoaded(result: SearchResult) {
		musicManager.stopLeavingWaiter()
		onCompletableSearchResultLoaded(result, musicManager.state.future)
	}

	/**
	 * Called when a playlist is successfully loaded. Stops the inactivity timer and handles the loaded playlist.
	 *
	 * @param result The loaded playlist result.
	 */
	final override fun onPlaylistLoaded(result: PlaylistLoaded) {
		musicManager.stopLeavingWaiter()
		onCompletablePlaylistLoaded(result, musicManager.state.future)
	}

	/**
	 * Called when loading fails. Completes the future with an error response based on the load failure details.
	 *
	 * @param result The load failure result.
	 */
	final override fun loadFailed(result: LoadFailed) {
		val details = onCompletableLoadFailed(result)
		musicManager.state.future.complete(onError(details))
	}

	/**
	 * Called when no matches are found during the load process. Completes the future with an error response.
	 */
	final override fun noMatches() {
		val details = onCompletableNoMatches()
		musicManager.state.future.complete(onError(details))
	}

	/**
	 * Handles the error case when loading fails or no matches are found. Logs the error and creates a command response.
	 *
	 * @param details The details of the load failure.
	 * @return A command response containing the error message.
	 */
	protected open fun onError(details: AudioLoadFailedDetails): CommandResponse {
		val context = musicManager.state.context
		val tracker = musicManager.beans.exceptionTrackerStore
		log.jdaError(context, details.logMessage, *(details.logArguments.toTypedArray()))
		return CommandResponse.Builder()
			.addEmbedMessages(tracker.createTrackerMessage(details.i18nLocaleSource, context, details.i18nArguments))
			.addActionRows(tracker.createTrackerLink(details.i18nLocaleSource))
			.build()
	}

	/**
	 * Called when a track is successfully loaded. Must be implemented by subclasses to handle the loaded track.
	 *
	 * @param result The loaded track result.
	 * @param future The future response associated with the audio command.
	 */
	protected abstract fun onCompletableTrackLoaded(result: TrackLoaded, future: TFutureResponse)

	/**
	 * Called when search results are successfully loaded. Must be implemented by subclasses to handle the search results.
	 *
	 * @param result The loaded search results.
	 * @param future The future response associated with the audio command.
	 */
	protected abstract fun onCompletableSearchResultLoaded(result: SearchResult, future: TFutureResponse)

	/**
	 * Called when a playlist is successfully loaded. Must be implemented by subclasses to handle the loaded playlist.
	 *
	 * @param result The loaded playlist result.
	 * @param future The future response associated with the audio command.
	 */
	protected abstract fun onCompletablePlaylistLoaded(result: PlaylistLoaded, future: TFutureResponse)

	/**
	 * Called when loading fails. Must be implemented by subclasses to provide details about the load failure.
	 *
	 * @param result The load failure result.
	 * @return Details about the audio load failure.
	 */
	protected abstract fun onCompletableLoadFailed(result: LoadFailed): AudioLoadFailedDetails

	/**
	 * Called when no matches are found during the load process. Must be implemented by subclasses to provide details.
	 *
	 * @return Details about the audio load failure when no matches are found.
	 */
	protected abstract fun onCompletableNoMatches(): AudioLoadFailedDetails
}
