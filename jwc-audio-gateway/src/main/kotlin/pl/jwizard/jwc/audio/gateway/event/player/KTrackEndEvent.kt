package pl.jwizard.jwc.audio.gateway.event.player

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.gateway.player.track.TrackEndReason

data class KTrackEndEvent(
	override val audioNode: AudioNode,
	override val guildId: Long,
	val track: Track,
	val endReason: TrackEndReason,
) : EmittedEvent() {
	companion object {
		fun fromProtocol(
			node: AudioNode,
			event: TrackEndEvent,
		) = KTrackEndEvent(
			node,
			event.guildId.toLong(),
			Track(event.track),
			TrackEndReason.fromProtocol(event.reason),
		)
	}
}
