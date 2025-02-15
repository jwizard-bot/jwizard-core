package pl.jwizard.jwc.audio.scheduler.repeat

class AudioTrackRepeat {
	var trackRepeat = false
		private set

	var playlistRepeat = false
		private set

	fun toggleTrackLoop(): Boolean {
		trackRepeat = !trackRepeat
		return trackRepeat
	}

	fun togglePlaylistLoop(): Boolean {
		playlistRepeat = !playlistRepeat
		return playlistRepeat
	}

	fun clear() {
		trackRepeat = false
		playlistRepeat = false
	}
}
