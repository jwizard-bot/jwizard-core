/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader

import pl.jwizard.jwac.AudioLoadResultHandler
import pl.jwizard.jwac.event.onload.*
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwl.util.logger

/**
 * Abstract base class for handling audio loading results and managing audio loading processes. This class extends
 * [AudioLoadResultHandler] to handle various audio load results and complete futures associated with audio
 * commands.
 *
 * @property guildMusicManager The guild music manager instance used for managing audio playback in the guild.
 * @author Miłosz Gilga
 */
abstract class AudioCompletableFutureLoader(
	private val guildMusicManager: GuildMusicManager,
) : AudioLoadResultHandler() {

	companion object {
		private val log = logger<AudioCompletableFutureLoader>()
	}

	/**
	 * Called when a track is successfully loaded. Stops the inactivity timer and handles the loaded track.
	 *
	 * @param result The loaded track result.
	 */
	final override fun onTrackLoaded(result: KTrackLoadedEvent) {
		guildMusicManager.stopLeavingWaiter()
		onCompletableTrackLoaded(result, guildMusicManager.state.future)
	}

	/**
	 * Called when search results are successfully loaded. Stops the inactivity timer and handles the search results.
	 *
	 * @param result The loaded search results.
	 */
	final override fun onSearchResultLoaded(result: KSearchResultEvent) {
		guildMusicManager.stopLeavingWaiter()
		onCompletableSearchResultLoaded(result, guildMusicManager.state.future)
	}

	/**
	 * Called when a playlist is successfully loaded. Stops the inactivity timer and handles the loaded playlist.
	 *
	 * @param result The loaded playlist result.
	 */
	final override fun onPlaylistLoaded(result: KPlaylistLoadedEvent) {
		guildMusicManager.stopLeavingWaiter()
		onCompletablePlaylistLoaded(result, guildMusicManager.state.future)
	}

	/**
	 * Called when loading fails. Completes the future with an error response based on the load failure details.
	 *
	 * @param result The load failure result.
	 */
	final override fun loadFailed(result: KLoadFailedEvent) = onError(details = onCompletableLoadFailed(result))

	/**
	 * Called when no matches are found during the load process. Completes the future with an error response.
	 *
	 * @param result The no matches result.
	 */
	final override fun noMatches(result: KNoMatchesEvent) = onError(details = onCompletableNoMatches(result))

	/**
	 * Handles the error case when loading fails or no matches are found. Logs the error and creates a command response.
	 *
	 * @param details The details of the load failure.
	 */
	protected open fun onError(details: AudioLoadFailedDetails) {
		val context = guildMusicManager.state.context
		val tracker = guildMusicManager.bean.exceptionTrackerHandler

		val response = CommandResponse.Builder()
			.addEmbedMessages(tracker.createTrackerMessage(details.i18nLocaleSource, context, details.i18nArguments))
			.addActionRows(tracker.createTrackerLink(details.i18nLocaleSource))
			.build()

		log.jdaError(context, details.logMessage, *(details.logArguments.toTypedArray()))
		guildMusicManager.state.future.complete(response)
	}

	/**
	 * Called when a track is successfully loaded. Must be implemented by subclasses to handle the loaded track.
	 *
	 * @param result The loaded track result.
	 * @param future The future response associated with the audio command.
	 */
	protected abstract fun onCompletableTrackLoaded(result: KTrackLoadedEvent, future: TFutureResponse)

	/**
	 * Called when search results are successfully loaded. Must be implemented by subclasses to handle the search results.
	 *
	 * @param result The loaded search results.
	 * @param future The future response associated with the audio command.
	 */
	protected abstract fun onCompletableSearchResultLoaded(result: KSearchResultEvent, future: TFutureResponse)

	/**
	 * Called when a playlist is successfully loaded. Must be implemented by subclasses to handle the loaded playlist.
	 *
	 * @param result The loaded playlist result.
	 * @param future The future response associated with the audio command.
	 */
	protected abstract fun onCompletablePlaylistLoaded(result: KPlaylistLoadedEvent, future: TFutureResponse)

	/**
	 * Called when loading fails. Must be implemented by subclasses to provide details about the load failure.
	 *
	 * @param result The load failure result.
	 * @return Details about the audio load failure.
	 */
	protected abstract fun onCompletableLoadFailed(result: KLoadFailedEvent): AudioLoadFailedDetails

	/**
	 * Called when no matches are found during the load process. Must be implemented by subclasses to provide details.
	 *
	 * @param result The no matches result.
	 * @return Details about the audio load failure when no matches are found.
	 */
	protected abstract fun onCompletableNoMatches(result: KNoMatchesEvent): AudioLoadFailedDetails
}
