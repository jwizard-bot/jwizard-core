/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for various audio-related messages.
 *
 * @property placeholder The placeholder key used to retrieve the localized message.
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 * @see I18nBean
 */
enum class I18nAudioSource(private val placeholder: String) : I18nLocaleSource {
	COUNT_OF_TRACKS("jwc.audio.countOfTracks"),
	TRACKS_TOTAL_DURATION_TIME("jwc.audio.totalDurationTime"),
	NEXT_TRACK_INDEX_MESS("jwc.audio.nextTrackIndex"),
	TRACK_ADDED_BY("jwc.audio.addedBy"),
	TRACK_NAME("jwc.audio.trackName"),
	TRACK_DURATION_TIME("jwc.audio.durationTime"),
	TRACK_POSITION_IN_QUEUE("jwc.audio.positionInQueue"),
	CURRENT_PLAYING_TRACK("jwc.audio.currentPlayingTrackDesc"),
	CURRENT_PAUSED_TRACK("jwc.audio.currentPausedTrackDesc"),
	CURRENT_PLAYING_TIMESTAMP("jwc.audio.currentPlayingTimestamp"),
	CURRENT_PAUSED_TIMESTAMP("jwc.audio.currentPausedTimestamp"),
	CURRENT_TRACK_LEFT_TO_NEXT("jwc.audio.currentTrackLeftToNext"),
	PAUSED_TRACK_TIME("jwc.audio.pausedTrackTime"),
	PAUSED_TRACK_ESTIMATE_TIME("jwc.audio.pausedTrackEstimateTime"),
	PAUSED_TRACK_TOTAL_DURATION("jwc.audio.pausedTrackTotalDuration"),
	ALL_TRACKS_IN_QUEUE_COUNT("jwc.audio.allTracksInQueueCount"),
	ALL_TRACKS_IN_QUEUE_DURATION("jwc.audio.allTracksInQueueDuration"),
	APPROX_TO_NEXT_TRACK_FROM_QUEUE("jwc.audio.approxToNextTrackFromQueue"),
	PLAYLIST_AVERAGE_TRACK_DURATION("jwc.audio.playlistAverageTrackDuration"),
	PLAYLIST_REPEATING_MODE("jwc.audio.playlistRepeatingMode"),
	;

	override fun getPlaceholder() = placeholder
}
