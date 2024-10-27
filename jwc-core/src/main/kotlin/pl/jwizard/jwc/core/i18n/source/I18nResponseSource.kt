/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwl.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for various response-related messages.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 */
enum class I18nResponseSource(override val placeholder: String) : I18nLocaleSource {

	// audio player
	LEAVE_EMPTY_CHANNEL("jwc.response.leaveEmptyChannel"),
	LEAVE_END_PLAYBACK_QUEUE("jwc.response.leaveEndPlaybackQueue"),
	ON_TRACK_START("jwc.response.onTrackStart"),
	ON_TRACK_START_ON_PAUSED("jwc.response.onTrackStartOnPause"),
	ON_END_PLAYBACK_QUEUE("jwc.response.onEndPlaybackQueue"),
	MULTIPLE_REPEATING_TRACK_INFO("jwc.response.multipleRepeatingTrackInfo"),
	PAUSE_TRACK_ON_FORCE_MUTE("jwc.response.pauseTrackOnForceMute"),
	RESUME_TRACK_ON_FORCE_UNMUTE("jwc.response.resumeTrackOnForceUnmute"),

	// radio station
	START_PLAYING_RADIO_STATION("jwc.response.startPlayingRadioStation"),
	START_PLAYING_RADIO_STATION_FIRST_OPTION("jwc.response.startPlayingRadioStationFirstOption"),
	START_PLAYING_RADIO_STATION_SECOND_OPTION("jwc.response.startPlayingRadioStationSecondOption"),
	STOP_PLAYING_RADIO_STATION("jwc.response.stopPlayingRadioStation"),
	CURRENTLY_PLAYING_STREAM_CONTENT("jwc.response.currentlyPlayingStreamContent"),
	RADIO_STATIONS_INFO("jwc.response.radioStationsInfo"),
	NO_RADIO_STATION_INFO("jwc.response.noRadioStationsInfo"),

	// dj
	ADD_PLAYLIST_TO_INFINITE_LOOP("jwc.response.addPlaylistToInfiniteLoop"),
	REMOVED_PLAYLIST_FROM_INFINITE_LOOP("jwc.response.removePlaylistFromInfiniteLoop"),
	MOVE_BOT_TO_SELECTED_CHANNEL("jwc.response.moveBotToSelectedChannel"),
	MOVE_TRACK_POS_TO_SELECTED_LOCATION("jwc.response.moveTrackPositionToSelectedLocation"),
	REMOVED_TRACKS_FROM_SELECTED_MEMBER("jwc.response.removedTracksFromSelectedMember"),
	RESET_AUDIO_PLAYER_VOLUME("jwc.response.resetAudioPlayerVolume"),
	SET_CURRENT_AUDIO_PLAYER_VOLUME("jwc.response.setCurrentAudioPlayerVolume"),
	QUEUE_WAS_SHUFFLED("jwc.response.queueWasShuffled"),
	SKIP_TO_SELECT_TRACK_POSITION("jwc.response.skipToSelectedTrackPosition"),
	SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE("jwc.response.skippedCurrentTrackAndClearQueue"),
	CLEAR_QUEUE("jwc.response.clearQueue"),

	// misc
	PRIVATE_MESSAGE_SEND("jwc.response.privateMessageSend"),
	CHECK_INBOX("jwc.response.checkInbox"),
	HELP("jwc.response.help"),
	HELPFUL_LINKS("jwc.response.helpfulLinks"),
	COMMANDS("jwc.response.commands"),
	BOT_WEBSITE("jwc.response.botWebsite"),
	BOT_SOURCE_CODE("jwc.response.botSourceCode"),
	BOT_DOCUMENTATION("jwc.response.botDocumentation"),
	VOTE_POLL("jwc.response.votePoll"),

	// music
	REMOVE_MULTIPLE_REPEATING_TRACK("jwc.response.removeMultipleRepeatingTrack"),
	GET_CURRENT_AUDIO_PLAYER_VOLUME("jwc.response.getCurrentAudioPlayerVolume"),
	ADD_TRACK_TO_INFINITE_LOOP("jwc.response.addTrackToInfiniteLoop"),
	REMOVED_TRACK_FROM_INFINITE_LOOP("jwc.response.removedTrackFromInfiniteLoop"),
	SET_MULTIPLE_REPEATING_TRACK("jwc.response.setMultipleRepeatingTrack"),
	RESUME_TRACK("jwc.response.resumeTrack"),
	SKIP_TRACK_AND_PLAY_NEXT("jwc.response.skipTrackAndPlayNext"),
	PAUSED_TRACK("jwc.response.pausedTrack"),
	SELECT_SONG_SEQUENCER("jwc.response.selectSongSequencer"),

	// playlist

	// vote
	VOTE_SHUFFLE_QUEUE("jwc.response.voteShuffleQueue"),
	FAILURE_VOTE_SHUFFLE_QUEUE("jwc.response.failureVoteShuffleQueue"),
	VOTE_SKIP_TRACK("jwc.response.voteSkipTrack"),
	FAILURE_VOTE_SKIP_TRACK("jwc.response.failureVoteSkipTrack"),
	VOTE_SKIP_TO_TRACK("jwc.response.voteSkipToTrack"),
	FAILURE_VOTE_SKIP_TO_TRACK("jwc.response.failureVoteSkipToTrack"),
	VOTE_CLEAR_QUEUE("jwc.response.voteClearQueue"),
	FAILURE_VOTE_CLEAR_QUEUE("jwc.response.failureVoteClearQueue"),
	VOTE_STOP_CLEAR_QUEUE("jwc.response.voteStopClearQueue"),
	FAILURE_VOTE_STOP_CLEAR_QUEUE("jwc.response.failureVoteStopClearQueue"),
	;
}
