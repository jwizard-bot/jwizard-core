package pl.jwizard.jwc.audio.gateway.event.player

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.WebSocketClosedEvent
import pl.jwizard.jwc.audio.gateway.node.AudioNode

data class KWsClosedEvent(
	override val audioNode: AudioNode,
	override val guildId: Long,
	val code: Int,
	val reason: String,
	val byRemote: Boolean,
) : EmittedEvent() {
	companion object {
		fun fromProtocol(
			node: AudioNode,
			event: WebSocketClosedEvent,
		) = KWsClosedEvent(node, event.guildId.toLong(), event.code, event.reason, event.byRemote)
	}
}
