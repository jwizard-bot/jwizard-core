package pl.jwizard.jwc.audio.gateway.event

import dev.arbjerg.lavalink.protocol.v4.Cpu
import dev.arbjerg.lavalink.protocol.v4.FrameStats
import dev.arbjerg.lavalink.protocol.v4.Memory
import dev.arbjerg.lavalink.protocol.v4.Message.StatsEvent
import pl.jwizard.jwc.audio.gateway.node.AudioNode

data class KStatsEvent(
	override val audioNode: AudioNode,
	val frameStats: FrameStats?,
	val players: Int,
	val playingPlayers: Int,
	val uptime: Long,
	val memory: Memory,
	val cpu: Cpu,
) : ClientEvent() {
	companion object {
		fun fromProtocol(
			node: AudioNode,
			event: StatsEvent,
		) = KStatsEvent(
			node,
			event.frameStats,
			event.players,
			event.playingPlayers,
			event.uptime,
			event.memory,
			event.cpu,
		)
	}
}
