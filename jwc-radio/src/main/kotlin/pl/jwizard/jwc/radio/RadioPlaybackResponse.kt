/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio

import java.time.Duration

/**
 * Represents radio playback information, including the current track, next track,
 * and metadata like thumbnail and playback times.
 *
 * @property title The title of the current track.
 * @property trackDuration The duration of the current track, or null if unknown.
 * @property nextPlay The title of the next track, or null if unavailable.
 * @property toNextPlayDuration The time until the next track, or null if unavailable.
 * @property streamThumbnailUrl The URL of the stream's thumbnail, or null if none.
 * @property elapsedNowSec The elapsed time of the current track in seconds, or null if unavailable.
 * @author Miłosz Gilga
 */
data class RadioPlaybackResponse(
	val title: String,
	val trackDuration: Duration? = null,
	val nextPlay: String? = null,
	val toNextPlayDuration: Duration? = null,
	val streamThumbnailUrl: String? = null,
	val elapsedNowSec: Duration? = null,
)
