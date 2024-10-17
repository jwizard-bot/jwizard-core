/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler

import dev.arbjerg.lavalink.client.player.FilterBuilder
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder
import dev.arbjerg.lavalink.client.player.Track
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.audio.spi.AudioScheduler
import reactor.core.Disposable

/**
 * Abstract class for handling audio scheduling in a music manager context.
 *
 * This class provides the base functionality for scheduling audio tracks and managing playback in a guild music manager
 * environment. It implements both the [AudioJobScheduler] and [AudioScheduler] interfaces, allowing for track
 * management and scheduling tasks.
 *
 * @property musicManager The [GuildMusicManager] instance used for managing music playback.
 * @author Miłosz Gilga
 */
abstract class AudioScheduleHandler(
	private val musicManager: GuildMusicManager
) : AudioJobScheduler, AudioScheduler {

	/**
	 * Stops the currently playing track and resets the audio player.
	 *
	 * This method sets the player to not paused, stops any currently playing track, and clears any filters applied to
	 * the audio player, effectively resetting it for a new track.
	 *
	 * @return The [PlayerUpdateBuilder] as an asynchronous response.
	 */
	override fun stopAndDestroy() = musicManager.createdOrUpdatedPlayer
		.setPaused(false)
		.stopTrack()
		.setFilters(FilterBuilder().build())

	/**
	 * Starts playback of the specified audio track.
	 *
	 * This method sets the provided track as the current track for the player and begins playback.
	 *
	 * @param track The [Track] instance to start playing.
	 * @return A [Disposable] that can be used to manage the subscription to this operation.
	 */
	protected fun startTrack(track: Track): Disposable = musicManager.createdOrUpdatedPlayer
		.setTrack(track)
		.subscribe()
}
