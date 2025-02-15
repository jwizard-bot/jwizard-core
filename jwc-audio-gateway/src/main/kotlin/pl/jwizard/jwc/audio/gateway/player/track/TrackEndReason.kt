package pl.jwizard.jwc.audio.gateway.player.track

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason

enum class TrackEndReason(
	val mayStartNext: Boolean,
) {
	// means that the track itself emitted a terminator
	// usually caused by the track reaching the end, however it will also be used when it ends due
	// to an exception.
	FINISHED(true),

	// means that the track failed to start, throwing an exception before providing any audio
	LOAD_FAILED(true),

	// track was stopped due to the player being stopped by either calling stop() or playTrack(null)
	STOPPED(false),

	// track stopped playing because a new track started playing
	// with this reason, the old track will still play until either its buffer runs out or audio
	// from the new track is available
	REPLACED(false),

	// stopped because the cleanup threshold for the audio player was reached
	// may also indicate either a leaked audio player which was discarded, but not stopped
	CLEANUP(false),
	;

	companion object {
		fun fromProtocol(reason: AudioTrackEndReason) = when (reason) {
			AudioTrackEndReason.FINISHED -> FINISHED
			AudioTrackEndReason.LOAD_FAILED -> LOAD_FAILED
			AudioTrackEndReason.STOPPED -> STOPPED
			AudioTrackEndReason.REPLACED -> REPLACED
			AudioTrackEndReason.CLEANUP -> CLEANUP
		}
	}
}
