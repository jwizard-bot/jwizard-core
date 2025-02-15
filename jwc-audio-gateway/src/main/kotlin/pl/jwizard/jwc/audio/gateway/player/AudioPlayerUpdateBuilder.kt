package pl.jwizard.jwc.audio.gateway.player

import dev.arbjerg.lavalink.protocol.v4.*
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.gateway.util.toJsonObject
import reactor.core.CoreSubscriber
import reactor.core.publisher.Mono

class AudioPlayerUpdateBuilder(
	private val guildId: Long,
	private val audioNode: AudioNode,
) : Mono<AudioPlayer>(), UpdatableAudioPlayer {
	private var trackUpdate: Omissible<PlayerUpdateTrack> = Omissible.omitted()
	private var position: Omissible<Long> = Omissible.omitted()
	private var endTime: Omissible<Long?> = Omissible.omitted()
	private var volume: Omissible<Int> = Omissible.omitted()
	private var paused: Omissible<Boolean> = Omissible.omitted()
	private var filters: Omissible<Filters> = Omissible.omitted()
	private var voiceState: Omissible<VoiceState> = Omissible.omitted()
	private var noReplace = false

	override fun setTrack(track: Track?) = apply {
		trackUpdate = PlayerUpdateTrack(
			encoded = Omissible.of(track?.encoded),
			userData = toJsonObject(track?.userData).toOmissible(),
		).toOmissible()
	}

	override fun updateTrack(track: PlayerUpdateTrack) = apply {
		track.toOmissible()
	}

	override fun stopTrack() = apply {
		trackUpdate = PlayerUpdateTrack(encoded = Omissible.of(null)).toOmissible()
	}

	override fun setPosition(position: Long?) = apply {
		this.position = position.toOmissible()
	}

	override fun setEndTime(endTime: Long?) = apply {
		this.endTime = endTime.toOmissible()
	}

	override fun omitEndTime() = apply {
		this.endTime = Omissible.omitted()
	}

	override fun setVolume(volume: Int) = apply {
		this.volume = volume.toOmissible()
	}

	override fun setPaused(paused: Boolean) = apply {
		this.paused = paused.toOmissible()
	}

	override fun setFilters(filters: Filters) = apply {
		this.filters = filters.toOmissible()
	}

	override fun setVoiceState(voiceState: VoiceState) = apply {
		this.voiceState = voiceState.toOmissible()
	}

	fun setNoReplace(noReplace: Boolean) = apply {
		this.noReplace = noReplace
	}

	fun applyBuilder(builder: AudioPlayerUpdateBuilder) = apply {
		this.trackUpdate = builder.trackUpdate
		this.position = builder.position
		this.endTime = builder.endTime
		this.volume = builder.volume
		this.paused = builder.paused
		this.filters = builder.filters
		this.voiceState = builder.voiceState
		this.noReplace = builder.noReplace
	}

	fun build() = PlayerUpdate(
		track = trackUpdate,
		position = position,
		endTime = endTime,
		volume = volume,
		paused = paused,
		filters = filters,
		voice = voiceState,
	)

	override fun subscribe(observer: CoreSubscriber<in AudioPlayer>) {
		audioNode.updatePlayer(guildId, build(), noReplace).subscribe(observer)
	}
}
