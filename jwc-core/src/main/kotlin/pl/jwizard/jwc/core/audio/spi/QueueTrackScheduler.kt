/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

/**
 * Interface for scheduling tracks in a queue with repeat functionality.
 *
 * The [QueueTrackScheduler] interface extends the [AudioScheduler] and is responsible for managing a queue of audio
 * tracks, including handling track repetition. It serves as a central point for interacting with the track queue and
 * the repeat behavior of the audio player.
 *
 * @author Miłosz Gilga
 */
interface QueueTrackScheduler : AudioScheduler {

	/**
	 * Gets the current track queue that is being managed by this scheduler.
	 */
	val queue: TrackQueue

	/**
	 * Gets the audio repeat settings for this scheduler, including options for repeating tracks or playlists.
	 */
	val audioRepeat: AudioRepeat

	/**
	 * Updates the count of times a track should be repeated.
	 *
	 * This method can be used to set the number of times a track in the queue should be repeated during playback.
	 *
	 * @param count The number of times to repeat the track. Must be a non-negative integer.
	 */
	fun updateCountOfRepeats(count: Int)
}
