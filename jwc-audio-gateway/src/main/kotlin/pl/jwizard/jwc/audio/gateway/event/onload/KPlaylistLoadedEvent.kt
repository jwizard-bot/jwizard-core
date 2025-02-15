package pl.jwizard.jwc.audio.gateway.event.onload

import dev.arbjerg.lavalink.protocol.v4.LoadResult.PlaylistLoaded
import dev.arbjerg.lavalink.protocol.v4.PlaylistInfo
import kotlinx.serialization.json.JsonObject
import pl.jwizard.jwc.audio.gateway.player.track.Track

data class KPlaylistLoadedEvent(
	val info: PlaylistInfo,
	val pluginInfo: JsonObject,
	val tracks: List<Track>,
) : KLoadResult {
	companion object {
		fun fromProtocol(
			event: PlaylistLoaded,
		) = KPlaylistLoadedEvent(
			event.data.info,
			event.data.pluginInfo,
			event.data.tracks.map(::Track),
		)
	}
}
