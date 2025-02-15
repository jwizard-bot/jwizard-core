package pl.jwizard.jwc.audio.gateway.event

import dev.arbjerg.lavalink.protocol.v4.Message.ReadyEvent
import pl.jwizard.jwc.audio.gateway.node.AudioNode

data class KReadyEvent(
	override val audioNode: AudioNode,
	val resumed: Boolean,
	val sessionId: String,
) : ClientEvent() {
	companion object {
		fun fromProtocol(
			node: AudioNode,
			event: ReadyEvent,
		) = KReadyEvent(node, event.resumed, event.sessionId)
	}
}
