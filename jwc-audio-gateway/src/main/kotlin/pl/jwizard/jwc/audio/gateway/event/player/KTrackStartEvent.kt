package pl.jwizard.jwc.audio.gateway.event.player

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackStartEvent
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track

data class KTrackStartEvent(
	override val audioNode: AudioNode,
	override val guildId: Long,
	val track: Track,
) : EmittedEvent() {
	companion object {
		fun fromProtocol(
			node: AudioNode,
			event: TrackStartEvent,
		) = KTrackStartEvent(node, event.guildId.toLong(), Track(event.track))
	}
}
