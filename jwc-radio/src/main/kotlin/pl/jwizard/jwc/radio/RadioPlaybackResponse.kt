package pl.jwizard.jwc.radio

import java.time.Duration

data class RadioPlaybackResponse(
	val title: String,
	val trackDuration: Duration? = null,
	val nextPlay: String? = null,
	val toNextPlayDuration: Duration? = null,
	val streamThumbnailUrl: String? = null,
	val elapsedNowSec: Duration? = null,
)
