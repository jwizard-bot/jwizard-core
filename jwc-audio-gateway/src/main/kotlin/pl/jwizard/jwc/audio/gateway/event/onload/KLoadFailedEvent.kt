package pl.jwizard.jwc.audio.gateway.event.onload

import dev.arbjerg.lavalink.protocol.v4.LoadResult.LoadFailed
import pl.jwizard.jwc.audio.gateway.player.track.TrackException

data class KLoadFailedEvent(val exception: TrackException) : KLoadResult {
	companion object {
		fun fromProtocol(
			event: LoadFailed,
		) = KLoadFailedEvent(TrackException.fromProtocol(event.data))
	}
}
