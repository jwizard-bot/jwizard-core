/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.i18n

enum class I18nResLocale(
	private val placeholder: String
) : I18nLocale {

	// audio player
	LEAVE_EMPTY_CHANNEL("pl.jwizard.response.leaveEmptyChannel"),
	LEAVE_END_PLAYBACK_QUEUE("pl.jwizard.response.leaveEndPlaybackQueue"),
	ON_TRACK_START("pl.jwizard.response.onTrackStart"),
	ON_TRACK_START_ON_PAUSED("pl.jwizard.response.onTrackStartOnPause"),
	ON_END_PLAYBACK_QUEUE("pl.jwizard.response.onEndPlaybackQueue"),
	MULTIPLE_REPEATING_TRACK_INFO("pl.jwizard.response.multipleRepeatingTrackInfo"),
	ADD_NEW_PLAYLIST("pl.jwizard.response.addNewPlaylist"),
	ADD_NEW_TRACK("pl.jwizard.response.addNewTrack"),
	PAUSE_TRACK_ON_FORCE_MUTE("pl.jwizard.response.pauseTrackOnForceMute"),
	RESUME_TRACK_ON_FORCE_UNMUTE("pl.jwizard.response.resumeTrackOnForceUnmute"),

	// dj commands
	ADD_PLAYLIST_TO_INFINITE_LOOP("pl.jwizard.response.addPlaylistToInfiniteLoop"),
	REMOVED_PLAYLIST_FROM_INFINITE_LOOP("pl.jwizard.response.removePlaylistFromInfiniteLoop"),
	MOVE_BOT_TO_SELECTED_CHANNEL("pl.jwizard.response.moveBotToSelectedChannel"),
	MOVE_TRACK_POS_TO_SELECTED_LOCATION("pl.jwizard.response.moveTrackPositionToSelectedLocation"),
	REMOVED_TRACKS_FROM_SELECTED_MEMBER("pl.jwizard.response.removedTracksFromSelectedMember"),
	RESET_AUDIO_PLAYER_VOLUME("pl.jwizard.response.resetAudioPlayerVolume"),
	SET_CURRENT_AUDIO_PLAYER_VOLUME("pl.jwizard.response.setCurrentAudioPlayerVolume"),
	QUEUE_WAS_SHUFFLED("pl.jwizard.response.queueWasShuffled"),
	SKIP_TO_SELECT_TRACK_POSITION("pl.jwizard.response.skipToSelectedTrackPosition"),
	SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE("pl.jwizard.response.skippedCurrentTrackAndClearQueue"),
	CLEAR_QUEUE("pl.jwizard.response.clearQueue"),

	// manager commands
	SYSTEM_DATA("pl.jwizard.response.systemData"),

	// misc commands
	COUNT_OF_AVAIALBLE_COMMANDS("pl.jwizard.response.counOfAvailableCommands"),

	// music commands
	REMOVE_MULTIPLE_REPEATING_TRACK("pl.jwizard.response.removeMultipleRepeatingTrack"),
	GET_CURRENT_AUDIO_PLAYER_VOLUME("pl.jwizard.response.getCurrentAudioPlayerVolume"),
	ADD_TRACK_TO_INFINITE_LOOP("pl.jwizard.response.addTrackToInfiniteLoop"),
	REMOVED_TRACK_FROM_INFINITE_LOOP("pl.jwizard.response.removedTrackFromInfiniteLoop"),
	SET_MULTIPLE_REPEATING_TRACK("pl.jwizard.response.setMultipleRepeatingTrack"),
	RESUME_TRACK("pl.jwizard.response.resumeTrack"),
	SKIP_TRACK_AND_PLAY_NEXT("pl.jwizard.response.skipTrackAndPlayNext"),
	PAUSED_TRACK("pl.jwizard.response.pausedTrack"),
	SELECT_SONG_SEQUENCER("pl.jwizard.response.selectSongSequencer"),

	// playlist commands

	// vote commands
	VOTE_SUFFLE_QUEUE("pl.jwizard.response.voteShuffleQueue"),
	SUCCESS_VOTE_SUFFLE_QUEUE("pl.jwizard.response.successVoteShuffleQueue"),
	FAILURE_VOTE_SUFFLE_QUEUE("pl.jwizard.response.failureVoteShuffleQueue"),
	VOTE_SKIP_TRACK("pl.jwizard.response.voteSkipTrack"),
	SUCCESS_VOTE_SKIP_TRACK("pl.jwizard.response.successVoteSkipTrack"),
	FAILURE_VOTE_SKIP_TRACK("pl.jwizard.response.failureVoteSkipTrack"),
	VOTE_SKIP_TO_TRACK("pl.jwizard.response.voteSkipToTrack"),
	SUCCESS_VOTE_SKIP_TO_TRACK("pl.jwizard.response.successVoteSkipToTrack"),
	FAILURE_VOTE_SKIP_TO_TRACK("pl.jwizard.response.failureVoteSkipToTrack"),
	VOTE_CLEAR_QUEUE("pl.jwizard.response.voteClearQueue"),
	SUCCESS_VOTE_CLEAR_QUEUE("pl.jwizard.response.successVoteClearQueue"),
	FAILURE_VOTE_CLEAR_QUEUE("pl.jwizard.response.failureVoteClearQueue"),
	VOTE_STOP_CLEAR_QUEUE("pl.jwizard.response.voteStopClearQueue"),
	SUCCESS_VOTE_STOP_CLEAR_QUEUE("pl.jwizard.response.successVoteStopClearQueue"),
	FAILURE_VOTE_STOP_CLEAR_QUEUE("pl.jwizard.response.failureVoteStopClearQueue"),
	;

	override fun getPlaceholder() = placeholder
}
