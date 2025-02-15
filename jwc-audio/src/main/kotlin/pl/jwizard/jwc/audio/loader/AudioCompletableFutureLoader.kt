package pl.jwizard.jwc.audio.loader

import pl.jwizard.jwc.audio.gateway.AudioLoadResultHandler
import pl.jwizard.jwc.audio.gateway.event.onload.*
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwl.util.logger

abstract class AudioCompletableFutureLoader(
	private val guildMusicManager: GuildMusicManager,
) : AudioLoadResultHandler() {
	companion object {
		private val log = logger<AudioCompletableFutureLoader>()
	}

	final override fun onTrackLoaded(result: KTrackLoadedEvent) {
		guildMusicManager.stopLeavingWaiter()
		onCompletableTrackLoaded(result, guildMusicManager.state.future)
	}

	final override fun onSearchResultLoaded(result: KSearchResultEvent) {
		guildMusicManager.stopLeavingWaiter()
		onCompletableSearchResultLoaded(result, guildMusicManager.state.future)
	}

	final override fun onPlaylistLoaded(result: KPlaylistLoadedEvent) {
		guildMusicManager.stopLeavingWaiter()
		onCompletablePlaylistLoaded(result, guildMusicManager.state.future)
	}

	final override fun loadFailed(
		result: KLoadFailedEvent,
	) = onError(details = onCompletableLoadFailed(result))

	final override fun noMatches(
		result: KNoMatchesEvent,
	) = onError(details = onCompletableNoMatches(result))

	protected open fun onError(details: AudioLoadFailedDetails) {
		val context = guildMusicManager.state.context
		val tracker = guildMusicManager.bean.exceptionTrackerHandler

		val response = CommandResponse.Builder().addEmbedMessages(
			tracker.createTrackerMessage(
				details.i18nLocaleSource, context, details.i18nArguments
			)
		).addActionRows(tracker.createTrackerLink(details.i18nLocaleSource, context)).build()

		log.jdaError(context, details.logMessage, *(details.logArguments.toTypedArray()))
		guildMusicManager.state.future.complete(response)
	}

	protected abstract fun onCompletableTrackLoaded(
		result: KTrackLoadedEvent,
		future: TFutureResponse,
	)

	protected abstract fun onCompletableSearchResultLoaded(
		result: KSearchResultEvent,
		future: TFutureResponse,
	)

	protected abstract fun onCompletablePlaylistLoaded(
		result: KPlaylistLoadedEvent,
		future: TFutureResponse,
	)

	protected abstract fun onCompletableLoadFailed(result: KLoadFailedEvent): AudioLoadFailedDetails

	protected abstract fun onCompletableNoMatches(result: KNoMatchesEvent): AudioLoadFailedDetails
}
