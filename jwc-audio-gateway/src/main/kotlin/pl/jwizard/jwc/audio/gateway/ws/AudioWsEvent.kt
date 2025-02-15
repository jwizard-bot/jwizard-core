package pl.jwizard.jwc.audio.gateway.ws

import dev.arbjerg.lavalink.protocol.v4.Message.*
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.*

internal interface AudioWsEvent {
	fun onReady(event: ReadyEvent)

	fun onStats(event: StatsEvent)

	fun onPlayerUpdate(event: PlayerUpdateEvent)

	fun onTrackStart(event: TrackStartEvent)

	fun onTrackEnd(event: TrackEndEvent)

	fun onWsClosed(event: WebSocketClosedEvent)
}
