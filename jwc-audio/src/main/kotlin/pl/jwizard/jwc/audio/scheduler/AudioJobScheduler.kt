package pl.jwizard.jwc.audio.scheduler

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason
import pl.jwizard.jwac.node.AudioNode
import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwac.player.track.TrackException

interface AudioJobScheduler {
	fun loadContent(tracks: List<Track>)

	fun onAudioStart(track: Track, audioNode: AudioNode)

	fun onAudioEnd(lastTrack: Track, audioNode: AudioNode, endReason: AudioTrackEndReason)

	fun onAudioStuck(track: Track, audioNode: AudioNode)

	fun onAudioException(track: Track, audioNode: AudioNode, exception: TrackException)
}
