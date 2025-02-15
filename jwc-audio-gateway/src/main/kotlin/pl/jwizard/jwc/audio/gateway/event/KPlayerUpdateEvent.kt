package pl.jwizard.jwc.audio.gateway.event

import dev.arbjerg.lavalink.protocol.v4.Message.PlayerUpdateEvent
import dev.arbjerg.lavalink.protocol.v4.PlayerState
import pl.jwizard.jwc.audio.gateway.node.AudioNode

data class KPlayerUpdateEvent(
	override val audioNode: AudioNode,
	val guildId: Long,
	val state: PlayerState,
) : ClientEvent() {
	companion object {
		fun fromProtocol(
			node: AudioNode,
			event: PlayerUpdateEvent,
		) = KPlayerUpdateEvent(node, event.guildId.toLong(), event.state)
	}
}
