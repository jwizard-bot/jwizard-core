/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

import dev.arbjerg.lavalink.client.player.Track

/**
 * Interface representing a queue of audio tracks for playback.
 *
 * The [TrackQueue] interface provides methods to manage a collection of audio tracks that can be played in sequence.
 * It allows operations such as retrieving, shuffling, skipping, and manipulating the order of tracks in the queue.
 *
 * @author Miłosz Gilga
 */
interface TrackQueue {

	/**
	 * Gets the current number of tracks in the queue.
	 */
	val size: Int

	/**
	 * Returns the tracks in the queue as an iterable list.
	 */
	val iterable: List<Track>

	/**
	 * Retrieves a track at the specified position in the queue.
	 *
	 * @param positionIndex The zero-based index of the track in the queue.
	 * @return The track located at the specified position.
	 * @throws IndexOutOfBoundsException If the positionIndex is out of bounds.
	 */
	fun getTrackByPosition(positionIndex: Int): Track

	/**
	 * Shuffles the order of the tracks in the queue. This method randomly rearranges the tracks to create a new
	 * playback order.
	 */
	fun shuffle()

	/**
	 * Skips to the specified position in the queue and returns the track at that position.
	 *
	 * @param position The zero-based index of the track to skip to.
	 * @return The track at the specified position, or null if the position is out of bounds.
	 */
	fun skipToPosition(position: Int): Track?

	/**
	 * Moves a track from one position to another within the queue.
	 *
	 * @param previous The zero-based index of the track to move.
	 * @param selected The zero-based index where the track should be moved to.
	 * @return The track that was moved.
	 * @throws IndexOutOfBoundsException If either position is out of bounds.
	 */
	fun moveToPosition(previous: Int, selected: Int): Track

	/**
	 * Removes all tracks from the queue that were added by the specified user.
	 *
	 * @param userId The ID of the user whose tracks should be removed.
	 * @return A list of tracks that were removed from the queue.
	 */
	fun removePositionsFromUser(userId: Long): List<Track>

	/**
	 * Checks if the specified position is out of the valid bounds of the queue.
	 *
	 * @param position The zero-based index to check.
	 * @return True if the position is out of bounds; otherwise, false.
	 */
	fun positionIsOutOfBounds(position: Int): Boolean

	/**
	 * Clears the queue and returns the current size of the queue.
	 *
	 * @return The size of the queue after clearing.
	 */
	fun clearAndGetSize(): Int
}
