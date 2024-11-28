/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwl.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for various audio-related messages.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 */
enum class I18nAudioSource(override val placeholder: String) : I18nLocaleSource {
	COUNT_OF_TRACKS("jw.audio.countOfTracks"),
	TRACKS_TOTAL_DURATION_TIME("jw.audio.totalDurationTime"),
	NEXT_TRACK_INDEX_MESS("jw.audio.nextTrackIndex"),
	TRACK_ADDED_BY("jw.audio.addedBy"),
	TRACK_NAME("jw.audio.trackName"),
	TRACK_DURATION_TIME("jw.audio.durationTime"),
	TRACK_POSITION_IN_QUEUE("jw.audio.positionInQueue"),
	CURRENT_PLAYING_TRACK("jw.audio.currentPlayingTrackDesc"),
	CURRENT_PAUSED_TRACK("jw.audio.currentPausedTrackDesc"),
	CURRENT_PLAYING_TIMESTAMP("jw.audio.currentPlayingTimestamp"),
	CURRENT_PAUSED_TIMESTAMP("jw.audio.currentPausedTimestamp"),
	CURRENT_TRACK_LEFT_TO_NEXT("jw.audio.currentTrackLeftToNext"),
	PAUSED_TRACK_TIME("jw.audio.pausedTrackTime"),
	PAUSED_TRACK_ESTIMATE_TIME("jw.audio.pausedTrackEstimateTime"),
	PAUSED_TRACK_TOTAL_DURATION("jw.audio.pausedTrackTotalDuration"),
	ALL_TRACKS_IN_QUEUE_COUNT("jw.audio.allTracksInQueueCount"),
	ALL_TRACKS_IN_QUEUE_DURATION("jw.audio.allTracksInQueueDuration"),
	APPROX_TO_NEXT_TRACK_FROM_QUEUE("jw.audio.approxToNextTrackFromQueue"),
	PLAYLIST_AVERAGE_TRACK_DURATION("jw.audio.playlistAverageTrackDuration"),
	PLAYLIST_REPEATING_MODE("jw.audio.playlistRepeatingMode"),
	ADD_NEW_PLAYLIST("jw.audio.addNewPlaylist"),
	ADD_NEW_TRACK("jw.audio.addNewTrack"),
	QUEUE("jw.audio.queue"),
	REMOVED_POSITIONS("jw.audio.removedPositions"),
	;
}
