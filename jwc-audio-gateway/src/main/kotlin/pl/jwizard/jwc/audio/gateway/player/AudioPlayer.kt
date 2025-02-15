package pl.jwizard.jwc.audio.gateway.player

import dev.arbjerg.lavalink.protocol.v4.*
import pl.jwizard.jwc.audio.gateway.balancer.region.VoiceRegion
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track
import kotlin.math.min

class AudioPlayer(
	private val audioNode: AudioNode,
	private val player: Player,
) : UpdatableAudioPlayer {
	val guildId = player.guildId.toLong()
	val volume = player.volume
	val voiceState = player.voice
	val filters = player.filters
	val paused = player.paused

	var state = player.state
		private set

	var track = player.track?.let { Track(it) }
		private set

	val position
		get() = when {
			player.track == null -> 0
			player.paused -> state.position
			// calculated elapsed time based on position and length of track
			else -> min(
				state.position + (System.currentTimeMillis() - state.time),
				player.track?.info!!.length
			)
		}

	val voiceRegion
		get() = if (voiceState.endpoint.isNotBlank()) {
			VoiceRegion.fromEndpoint(voiceState.endpoint)
		} else {
			null
		}

	internal fun updateState(state: PlayerState) {
		this.state = state
	}

	internal fun updateTrack(track: Track?) {
		this.track = track
	}

	override fun setTrack(
		track: Track?,
	) = AudioPlayerUpdateBuilder(guildId, audioNode).setTrack(track)

	override fun updateTrack(
		track: PlayerUpdateTrack,
	) = AudioPlayerUpdateBuilder(guildId, audioNode).updateTrack(track)

	override fun stopTrack() = AudioPlayerUpdateBuilder(guildId, audioNode).stopTrack()

	override fun setPosition(
		position: Long?,
	) = AudioPlayerUpdateBuilder(guildId, audioNode).setPosition(position)

	override fun setEndTime(
		endTime: Long?,
	) = AudioPlayerUpdateBuilder(guildId, audioNode).setEndTime(endTime)

	override fun omitEndTime() = AudioPlayerUpdateBuilder(guildId, audioNode).omitEndTime()

	override fun setVolume(
		volume: Int,
	) = AudioPlayerUpdateBuilder(guildId, audioNode).setVolume(volume)

	override fun setPaused(
		paused: Boolean,
	) = AudioPlayerUpdateBuilder(guildId, audioNode).setPaused(paused)

	override fun setFilters(
		filters: Filters,
	) = AudioPlayerUpdateBuilder(guildId, audioNode).setFilters(filters)

	override fun setVoiceState(
		voiceState: VoiceState,
	) = AudioPlayerUpdateBuilder(guildId, audioNode).setVoiceState(voiceState)

	fun stateToBuilder() = AudioPlayerUpdateBuilder(guildId, audioNode)
		.apply { player.track?.let { setTrack(Track(it)) } }
		.setPosition(position)
		.setEndTime(null)
		.setVolume(player.volume)
		.setPaused(player.paused)
		.setFilters(player.filters)
		.setVoiceState(player.voice)
}
