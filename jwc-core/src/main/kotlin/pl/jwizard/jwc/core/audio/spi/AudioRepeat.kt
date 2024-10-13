/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

/**
 * Interface for managing audio repeat functionality in an audio player.
 *
 * This interface provides methods and properties to control the looping of tracks and playlists. Implementing classes
 * should provide the logic for toggling repeat states and retrieving the current repeat status.
 *
 * @author Miłosz Gilga
 */
interface AudioRepeat {

	/**
	 * Indicates whether the current track is set to repeat.
	 *
	 * @return `true` if the current track will loop when it ends; `false` otherwise.
	 */
	val trackRepeat: Boolean

	/**
	 * Indicates whether the current playlist is set to repeat.
	 *
	 * @return `true` if the playlist will loop when it reaches the end; `false` otherwise.
	 */
	val playlistRepeat: Boolean

	/**
	 * Toggles the repeat state for the current track.
	 *
	 * This method changes the [trackRepeat] state and returns the new state.
	 *
	 * @return `true` if track repeat is now enabled; `false` if it has been disabled.
	 */
	fun toggleTrackLoop(): Boolean

	/**
	 * Toggles the repeat state for the current playlist.
	 *
	 * This method changes the [playlistRepeat] state and returns the new state.
	 *
	 * @return `true` if playlist repeat is now enabled; `false` if it has been disabled.
	 */
	fun togglePlaylistLoop(): Boolean
}
