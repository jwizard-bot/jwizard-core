/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler

import dev.arbjerg.lavalink.client.player.FilterBuilder
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder
import dev.arbjerg.lavalink.client.player.Track
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import reactor.core.Disposable
import reactor.core.publisher.Mono

/**
 * TODO
 *
 * @property guildMusicManager The [GuildMusicManager] instance used for managing music playback.
 * @author Miłosz Gilga
 */
abstract class AudioScheduleHandler(private val guildMusicManager: GuildMusicManager) : AudioJobScheduler {

	/**
	 * Stops the currently playing track and resets the audio player.
	 *
	 * This method sets the player to not paused, stops any currently playing track, and clears any filters applied to
	 * the audio player, effectively resetting it for a new track.
	 *
	 * @return The [Mono] as an asynchronous response.
	 */
	open fun stopAndDestroy(): PlayerUpdateBuilder {
		guildMusicManager.state.clearAudioType()
		return guildMusicManager.createdOrUpdatedPlayer
			.setPaused(false)
			.stopTrack()
			.setFilters(FilterBuilder().build())
	}

	/**
	 * Starts playback of the specified audio track.
	 *
	 * This method sets the provided track as the current track for the player and begins playback.
	 *
	 * @param track The [Track] instance to start playing.
	 * @return A [Disposable] that can be used to manage the subscription to this operation.
	 */
	protected fun startTrack(track: Track): Disposable = guildMusicManager.createdOrUpdatedPlayer
		.setTrack(track)
		.subscribe()
}
