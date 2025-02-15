package pl.jwizard.jwc.audio.gateway

import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.arbjerg.lavalink.protocol.v4.LoadResult.*
import pl.jwizard.jwc.audio.gateway.event.onload.*
import java.util.function.Consumer

// wrapper for WS load events
abstract class AudioLoadResultHandler : Consumer<LoadResult> {
	override fun accept(loadResult: LoadResult) {
		when (loadResult) {
			is TrackLoaded -> onTrackLoaded(KTrackLoadedEvent.fromProtocol(loadResult))
			is LoadFailed -> loadFailed(KLoadFailedEvent.fromProtocol(loadResult))
			is NoMatches -> noMatches(KNoMatchesEvent())
			is PlaylistLoaded -> onPlaylistLoaded(KPlaylistLoadedEvent.fromProtocol(loadResult))
			is SearchResult -> onSearchResultLoaded(KSearchResultEvent.fromProtocol(loadResult))
		}
	}

	protected abstract fun onTrackLoaded(result: KTrackLoadedEvent)

	protected abstract fun onPlaylistLoaded(result: KPlaylistLoadedEvent)

	protected abstract fun onSearchResultLoaded(result: KSearchResultEvent)

	protected abstract fun noMatches(result: KNoMatchesEvent)

	protected abstract fun loadFailed(result: KLoadFailedEvent)
}
