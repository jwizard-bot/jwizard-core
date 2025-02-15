package pl.jwizard.jwc.audio.gateway.event.player

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackStuckEvent
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track

data class KTrackStuckEvent(
	override val audioNode: AudioNode,
	override val guildId: Long,
	val track: Track,
	val thresholdMs: Long,
) : EmittedEvent() {
	companion object {
		fun fromProtocol(
			node: AudioNode,
			event: TrackStuckEvent,
		) = KTrackStuckEvent(node, event.guildId.toLong(), Track(event.track), event.thresholdMs)
	}
}
