/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler

import dev.arbjerg.lavalink.client.player.Track
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.audio.spi.TrackQueue
import java.util.*

/**
 * Represents a queue of audio tracks for a music manager.
 *
 * This class extends [LinkedList] and implements the [TrackQueue] interface, providing functionality for managing a
 * queue of audio tracks, including methods for shuffling, skipping, and removing tracks.
 *
 * @property musicManager The [MusicManager] instance used for managing audio tracks and interactions.
 * @constructor Creates an instance of [AudioTrackQueue] with the specified music manager.
 */
class AudioTrackQueue(private val musicManager: MusicManager) : LinkedList<Track>(), TrackQueue {

	/**
	 * Returns the tracks in the queue as an iterable list.
	 */
	override val iterable
		get() = toList()

	/**
	 * Retrieves the track at the specified position in the queue.
	 *
	 * @param positionIndex The position of the track (1-based index).
	 * @return The [Track] at the specified position.
	 * @throws IndexOutOfBoundsException if the positionIndex is out of range.
	 */
	override fun getTrackByPosition(positionIndex: Int): Track = ArrayList(this)[positionIndex - 1]

	/**
	 * Shuffles the order of the tracks in the queue. This method randomizes the order of tracks in the queue using
	 * the built-in shuffle functionality.
	 */
	override fun shuffle() = (this as MutableList<*>).shuffle()

	/**
	 * Skips to the specified position in the queue and returns the track at that position.
	 *
	 * @param position The position to skip to (0-based index).
	 * @return The [Track] at the specified position, or null if the position is invalid.
	 */
	override fun skipToPosition(position: Int): Track? {
		var track: Track? = null
		for (i in 0..position) {
			track = poll()
		}
		return track
	}

	/**
	 * Moves a track from one position to another within the queue.
	 *
	 * @param previous The current position of the track (1-based index).
	 * @param selected The new position for the track (1-based index).
	 * @return The [Track] that was moved.
	 * @throws IndexOutOfBoundsException if either position is out of bounds.
	 */
	override fun moveToPosition(previous: Int, selected: Int): Track {
		val copiedTracks = ArrayList(this)
		val selectedTrack = copiedTracks.removeAt(previous - 1)
		copiedTracks.add(selected - 1, selectedTrack)
		clear()
		addAll(copiedTracks)
		return selectedTrack
	}

	/**
	 * Removes all tracks from the queue that were added by the specified user.
	 *
	 * @param userId The ID of the user whose tracks should be removed.
	 * @return A list of tracks that were removed from the queue.
	 */
	override fun removePositionsFromUser(userId: Long): List<Track> {
		val copiedTracks = ArrayList(this)
		val omitFromMember = copiedTracks.filter { musicManager.getAudioSenderId(it) != userId }
		clear()
		addAll(omitFromMember)
		return copiedTracks - omitFromMember.toSet()
	}

	/**
	 * Checks if the specified position is out of bounds for the current queue size.
	 *
	 * @param position The position to check (1-based index).
	 * @return True if the position is out of bounds; false otherwise.
	 */
	override fun positionIsOutOfBounds(position: Int) = position < 1 || position > size

	/**
	 * Clears the queue and returns the size of the queue before clearing.
	 *
	 * @return The size of the queue before it was cleared.
	 */
	override fun clearAndGetSize(): Int {
		val queueSize = size
		clear()
		return queueSize
	}
}
