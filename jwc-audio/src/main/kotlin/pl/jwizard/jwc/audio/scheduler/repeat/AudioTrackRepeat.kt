/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler.repeat

/**
 * Implementation of the AudioRepeat interface for managing track and playlist repetition. This class allows toggling
 * the repeat status for both individual tracks and entire playlists.
 *
 * @author Miłosz Gilga
 */
class AudioTrackRepeat {

	/**
	 * Indicates whether the current track should be repeated.
	 */
	var trackRepeat = false
		private set

	/**
	 * Indicates whether the current playlist should be repeated.
	 */
	var playlistRepeat = false
		private set

	/**
	 * Toggles the repeat status of the current track.
	 *
	 * @return The new status of track repetition after toggling (true if now repeating, false otherwise).
	 */
	fun toggleTrackLoop(): Boolean {
		trackRepeat = !trackRepeat
		return trackRepeat
	}

	/**
	 * Toggles the repeat status of the current playlist.
	 *
	 * @return The new status of playlist repetition after toggling (true if now repeating, false otherwise).
	 */
	fun togglePlaylistLoop(): Boolean {
		playlistRepeat = !playlistRepeat
		return playlistRepeat
	}

	/**
	 * Resets the repeat status for both track and playlist to false.
	 *
	 * This method clears any repeat settings, ensuring that no tracks or playlists are set to repeat.
	 */
	fun clear() {
		trackRepeat = false
		playlistRepeat = false
	}
}
