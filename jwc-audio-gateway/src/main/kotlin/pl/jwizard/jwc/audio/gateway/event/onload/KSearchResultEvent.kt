package pl.jwizard.jwc.audio.gateway.event.onload

import dev.arbjerg.lavalink.protocol.v4.LoadResult.SearchResult
import pl.jwizard.jwc.audio.gateway.player.track.Track

data class KSearchResultEvent(val tracks: List<Track>) : KLoadResult {
	companion object {
		fun fromProtocol(event: SearchResult) = KSearchResultEvent(event.data.tracks.map(::Track))
	}
}
