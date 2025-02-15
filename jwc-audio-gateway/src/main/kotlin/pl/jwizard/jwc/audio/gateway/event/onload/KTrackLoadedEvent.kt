package pl.jwizard.jwc.audio.gateway.event.onload

import dev.arbjerg.lavalink.protocol.v4.LoadResult.TrackLoaded
import pl.jwizard.jwc.audio.gateway.player.track.Track

data class KTrackLoadedEvent(val track: Track) : KLoadResult {
	companion object {
		fun fromProtocol(result: TrackLoaded) = KTrackLoadedEvent(Track(result.data))
	}
}
