package pl.jwizard.jwc.audio.gateway.event.player

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackExceptionEvent
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.gateway.player.track.TrackException

data class KTrackExceptionEvent(
	override val audioNode: AudioNode,
	override val guildId: Long,
	val track: Track,
	val exception: TrackException,
) : EmittedEvent() {
	companion object {
		fun fromProtocol(
			node: AudioNode,
			event: TrackExceptionEvent,
		) = KTrackExceptionEvent(
			node,
			guildId = event.guildId.toLong(),
			track = Track(event.track),
			exception = TrackException.fromProtocol(event.exception),
		)
	}
}
