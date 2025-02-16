package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwl.i18n.I18nLocaleSource

enum class I18nResponseSource(override val placeholder: String) : I18nLocaleSource {
	// audio player
	LEAVE_EMPTY_CHANNEL("jw.response.leaveEmptyChannel"),
	LEAVE_END_PLAYBACK_QUEUE("jw.response.leaveEndPlaybackQueue"),
	ON_TRACK_START("jw.response.onTrackStart"),
	ON_TRACK_START_ON_PAUSED("jw.response.onTrackStartOnPause"),
	ON_END_PLAYBACK_QUEUE("jw.response.onEndPlaybackQueue"),
	MULTIPLE_REPEATING_TRACK_INFO("jw.response.multipleRepeatingTrackInfo"),
	PAUSE_TRACK_ON_FORCE_MUTE("jw.response.pauseTrackOnForceMute"),
	RESUME_TRACK_ON_FORCE_UNMUTE("jw.response.resumeTrackOnForceUnmute"),

	// radio station
	START_PLAYING_RADIO_STATION("jw.response.startPlayingRadioStation"),
	START_PLAYING_RADIO_STATION_FIRST_OPTION("jw.response.startPlayingRadioStationFirstOption"),
	START_PLAYING_RADIO_STATION_SECOND_OPTION("jw.response.startPlayingRadioStationSecondOption"),
	STOP_PLAYING_RADIO_STATION("jw.response.stopPlayingRadioStation"),
	CURRENTLY_PLAYING_STREAM_CONTENT("jw.response.currentlyPlayingStreamContent"),
	RADIO_STATIONS_INFO("jw.response.radioStationsInfo"),
	NO_RADIO_STATION_INFO("jw.response.noRadioStationsInfo"),

	// dj
	ADD_PLAYLIST_TO_INFINITE_LOOP("jw.response.addPlaylistToInfiniteLoop"),
	REMOVED_PLAYLIST_FROM_INFINITE_LOOP("jw.response.removePlaylistFromInfiniteLoop"),
	MOVE_BOT_TO_SELECTED_CHANNEL("jw.response.moveBotToSelectedChannel"),
	MOVE_TRACK_POS_TO_SELECTED_LOCATION("jw.response.moveTrackPositionToSelectedLocation"),
	REMOVED_TRACKS_FROM_SELECTED_MEMBER("jw.response.removedTracksFromSelectedMember"),
	RESET_AUDIO_PLAYER_VOLUME("jw.response.resetAudioPlayerVolume"),
	SET_CURRENT_AUDIO_PLAYER_VOLUME("jw.response.setCurrentAudioPlayerVolume"),
	QUEUE_WAS_SHUFFLED("jw.response.queueWasShuffled"),
	SKIP_TO_SELECT_TRACK_POSITION("jw.response.skipToSelectedTrackPosition"),
	SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE("jw.response.skippedCurrentTrackAndClearQueue"),
	CLEAR_QUEUE("jw.response.clearQueue"),

	// misc
	PRIVATE_MESSAGE_SEND("jw.response.privateMessageSend"),
	CHECK_INBOX("jw.response.checkInbox"),
	HELP("jw.response.help"),
	HELPFUL_LINKS("jw.response.helpfulLinks"),
	COMMANDS("jw.response.commands"),
	BOT_WEBSITE("jw.response.botWebsite"),
	INFRA_CURRENT_STATUS("jw.response.infraCurrentStatus"),
	BOT_SOURCE_CODE("jw.response.botSourceCode"),
	VOTE_POLL("jw.response.votePoll"),

	// music
	REMOVE_MULTIPLE_REPEATING_TRACK("jw.response.removeMultipleRepeatingTrack"),
	GET_CURRENT_AUDIO_PLAYER_VOLUME("jw.response.getCurrentAudioPlayerVolume"),
	ADD_TRACK_TO_INFINITE_LOOP("jw.response.addTrackToInfiniteLoop"),
	REMOVED_TRACK_FROM_INFINITE_LOOP("jw.response.removedTrackFromInfiniteLoop"),
	SET_MULTIPLE_REPEATING_TRACK("jw.response.setMultipleRepeatingTrack"),
	RESUME_TRACK("jw.response.resumeTrack"),
	SKIP_TRACK_AND_PLAY_NEXT("jw.response.skipTrackAndPlayNext"),
	PAUSED_TRACK("jw.response.pausedTrack"),
	SELECT_SONG_SEQUENCER("jw.response.selectSongSequencer"),

	// playlist
	// TODO

	// vote
	VOTE_SHUFFLE_QUEUE("jw.response.voteShuffleQueue"),
	FAILURE_VOTE_SHUFFLE_QUEUE("jw.response.failureVoteShuffleQueue"),
	VOTE_SKIP_TRACK("jw.response.voteSkipTrack"),
	FAILURE_VOTE_SKIP_TRACK("jw.response.failureVoteSkipTrack"),
	VOTE_SKIP_TO_TRACK("jw.response.voteSkipToTrack"),
	FAILURE_VOTE_SKIP_TO_TRACK("jw.response.failureVoteSkipToTrack"),
	VOTE_CLEAR_QUEUE("jw.response.voteClearQueue"),
	FAILURE_VOTE_CLEAR_QUEUE("jw.response.failureVoteClearQueue"),
	VOTE_STOP_CLEAR_QUEUE("jw.response.voteStopClearQueue"),
	FAILURE_VOTE_STOP_CLEAR_QUEUE("jw.response.failureVoteStopClearQueue"),
	;
}
