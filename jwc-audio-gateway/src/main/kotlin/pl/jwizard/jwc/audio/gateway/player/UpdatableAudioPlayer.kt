package pl.jwizard.jwc.audio.gateway.player

import dev.arbjerg.lavalink.protocol.v4.Filters
import dev.arbjerg.lavalink.protocol.v4.PlayerUpdateTrack
import dev.arbjerg.lavalink.protocol.v4.VoiceState
import pl.jwizard.jwc.audio.gateway.player.track.Track

interface UpdatableAudioPlayer {
	fun setTrack(track: Track?): AudioPlayerUpdateBuilder

	fun updateTrack(track: PlayerUpdateTrack): AudioPlayerUpdateBuilder

	fun stopTrack(): AudioPlayerUpdateBuilder

	fun setPosition(position: Long?): AudioPlayerUpdateBuilder

	fun setEndTime(endTime: Long?): AudioPlayerUpdateBuilder

	fun omitEndTime(): AudioPlayerUpdateBuilder

	fun setVolume(volume: Int): AudioPlayerUpdateBuilder

	fun setPaused(paused: Boolean): AudioPlayerUpdateBuilder

	fun setFilters(filters: Filters): AudioPlayerUpdateBuilder

	fun setVoiceState(voiceState: VoiceState): AudioPlayerUpdateBuilder
}
