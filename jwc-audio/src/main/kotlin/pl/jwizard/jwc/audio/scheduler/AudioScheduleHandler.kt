package pl.jwizard.jwc.audio.scheduler

import pl.jwizard.jwac.player.FilterBuilder
import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import reactor.core.Disposable
import reactor.core.publisher.Mono

abstract class AudioScheduleHandler(
	private val guildMusicManager: GuildMusicManager,
) : AudioJobScheduler {
	open fun stopAndDestroy(): Mono<*> {
		guildMusicManager.state.clearAudioType()
		return guildMusicManager.createdOrUpdatedPlayer
			.setPaused(false)
			.stopTrack()
			.setFilters(FilterBuilder().build())
	}

	protected fun startTrack(track: Track): Disposable = guildMusicManager.createdOrUpdatedPlayer
		.setTrack(track)
		.subscribe()
}
