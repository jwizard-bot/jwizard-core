package pl.jwizard.jwc.audio.gateway.ws

import dev.arbjerg.lavalink.protocol.v4.Message.*
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.*
import pl.jwizard.jwc.audio.gateway.AudioClient
import pl.jwizard.jwc.audio.gateway.link.LinkState
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.gateway.util.isEmpty
import pl.jwizard.jwl.util.logger
import dev.arbjerg.lavalink.protocol.v4.Track as ProtocolTrack

internal class AudioWsEventImpl(
	private val audioNode: AudioNode,
	private val audioClient: AudioClient,
) : AudioWsEvent {
	companion object {
		private val log = logger<AudioWsEventImpl>()
	}

	// ws close codes, that indicate the connection was closed due to some error
	// only for these codes, link with active players will be destroyed
	private val wsEndingCodes = listOf(4004, 4009)

	override fun onReady(event: ReadyEvent) {
		if (!event.resumed) {
			audioNode.penalties.resetMetrics()
		}
		audioNode.sessionId = event.sessionId
		audioNode.available = true

		log.info("Node: {} is ready with session id: {}.", audioNode, event.sessionId)

		for (player in audioNode.players.values) {
			// if player has not bound gateway endpoint, omit
			if (player.voiceState.isEmpty()) {
				continue
			}
			player.stateToBuilder()
				.setNoReplace(false)
				.subscribe()
		}
		audioNode.transferOrphansToSelf()
	}

	override fun onStats(event: StatsEvent) {
		audioNode.stats = event
	}

	override fun onPlayerUpdate(event: PlayerUpdateEvent) {
		val guildIdLong = event.guildId.toLong()

		val player = audioNode.getCachedPlayer(guildIdLong)
		val link = audioClient.getLinkIfCached(guildIdLong)

		val linkState = if (event.state.connected) {
			LinkState.CONNECTED
		} else {
			LinkState.DISCONNECTED
		}
		player?.updateState(event.state)
		link?.updateState(linkState)
	}

	override fun onTrackStart(event: TrackStartEvent) = updateTrack(event, event.track)

	override fun onTrackEnd(event: TrackEndEvent) = updateTrack(event, null)

	override fun onWsClosed(event: WebSocketClosedEvent) {
		log.debug(
			"Close WS connection with code: {}. Cause: {}. By remote: {}.",
			event.code,
			event.reason,
			event.byRemote
		)
		if (wsEndingCodes.contains(event.code)) {
			log.debug(
				"Node: {} received close code: {} for guild: {}.",
				audioNode,
				event.code,
				event.guildId
			)
			audioNode.destroyPlayerAndLink(event.guildId.toLong()).subscribe()
		}
	}

	private fun updateTrack(event: EmittedEvent, track: ProtocolTrack?) {
		val player = audioNode.getCachedPlayer(event.guildId.toLong())
		player?.updateTrack(if (track == null) null else Track(track))
	}
}
