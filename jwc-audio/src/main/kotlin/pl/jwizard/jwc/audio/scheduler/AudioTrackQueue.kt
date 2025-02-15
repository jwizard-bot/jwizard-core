package pl.jwizard.jwc.audio.scheduler

import pl.jwizard.jwac.player.track.Track
import java.util.*

class AudioTrackQueue : LinkedList<Track>() {

	val iterable
		get() = toList()

	fun getTrackByPosition(positionIndex: Int): Track = ArrayList(this)[positionIndex - 1]

	fun shuffle() = (this as MutableList<*>).shuffle()

	fun skipToPosition(position: Int): Track? {
		var track: Track? = null
		for (i in 1..position) {
			track = poll()
		}
		return track
	}

	fun moveToPosition(previous: Int, selected: Int): Track {
		val copiedTracks = ArrayList(this)
		val selectedTrack = copiedTracks.removeAt(previous - 1)
		copiedTracks.add(selected - 1, selectedTrack)
		clear()
		addAll(copiedTracks)
		return selectedTrack
	}

	fun removePositionsFromUser(userId: Long): List<Track> {
		val copiedTracks = ArrayList(this)
		val omitFromMember = copiedTracks.filter { it.audioSender.authorId != userId }
		clear()
		addAll(omitFromMember)
		return copiedTracks - omitFromMember.toSet()
	}

	fun positionIsOutOfBounds(position: Int) = position < 1 || position > size

	fun clearAndGetSize(): Int {
		val queueSize = size
		clear()
		return queueSize
	}
}
