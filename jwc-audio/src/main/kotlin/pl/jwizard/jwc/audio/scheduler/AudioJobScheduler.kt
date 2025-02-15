package pl.jwizard.jwc.audio.scheduler

import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.gateway.player.track.TrackEndReason
import pl.jwizard.jwc.audio.gateway.player.track.TrackException

internal interface AudioJobScheduler {
	fun loadContent(tracks: List<Track>)

	fun onAudioStart(track: Track, audioNode: AudioNode)

	fun onAudioEnd(lastTrack: Track, audioNode: AudioNode, endReason: TrackEndReason)

	fun onAudioStuck(track: Track, audioNode: AudioNode)

	fun onAudioException(track: Track, audioNode: AudioNode, exception: TrackException)
}
