/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandLocale.java
 * Last modified: 04/04/2023, 18:15
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.locale;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import pl.miloszgilga.core.IEnumerableLocaleSet;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum ResLocaleSet implements IEnumerableLocaleSet {

    ADD_NEW_TRACK_MESS                              ("jwizard.message.response.AddNewTrack"),
    TRACK_NAME_MESS                                 ("jwizard.message.response.TrackName"),
    TRACK_DURATION_TIME_MESS                        ("jwizard.message.response.DurationTime"),
    TRACK_POSITION_IN_QUEUE_MESS                    ("jwizard.message.response.PositionInQueue"),
    ADD_NEW_PLAYLIST_MESS                           ("jwizard.message.response.AddNewPlaylist"),
    COUNT_OF_TRACKS_MESS                            ("jwizard.message.response.CountOfTracks"),
    TRACKS_TOTAL_DURATION_TIME_MESS                 ("jwizard.message.response.TotalDurationTime"),
    TRACK_ADDDED_BY_MESS                            ("jwizard.message.response.AddedBy"),
    NEXT_TRACK_INDEX_MESS                           ("jwizard.message.response.NextTrackIndex"),
    PLAYLIST_AVERAGE_TRACK_DURATION_MESS            ("jwizard.message.response.PlaylistAverageTrackDuration"),
    PLAYLIST_REPEATING_MODE_MESS                    ("jwizard.message.response.PlaylistRepeatingMode"),
    NOT_FOUND_TRACK_MESS                            ("jwizard.message.response.NotFoundAudioTrack"),
    ISSUE_WHILE_LOADING_TRACK_MESS                  ("jwizard.message.response.UnexpectedErrorOnLoadTrack"),
    ISSUE_WHILE_PLAYING_TRACK_MESS                  ("jwizard.message.response.UnexpectedErrorOnPlayTrack"),
    BUG_TRACKER_MESS                                ("jwizard.message.response.BugTracker"),
    PAUSE_TRACK_MESS                                ("jwizard.message.response.PauseTrack"),
    RESUME_TRACK_MESS                               ("jwizard.message.response.ResumeTrack"),
    LEAVE_EMPTY_CHANNEL_MESS                        ("jwizard.message.response.LeaveEmptyChannel"),
    ON_TRACK_START_MESS                             ("jwizard.message.response.OnTrackStart"),
    ON_TRACK_START_ON_PAUSED_MESS                   ("jwizard.message.response.OnTrackStartOnPause"),
    ON_END_PLAYBACK_QUEUE_MESS                      ("jwizard.message.response.OnEndPlaybackQueue"),
    LEAVE_END_PLAYBACK_QUEUE_MESS                   ("jwizard.message.response.LeaveEndPlaybackQueue"),
    ADD_TRACK_TO_INFINITE_LOOP_MESS                 ("jwizard.message.response.AddTrackToInfiniteLoop"),
    REMOVE_TRACK_FROM_INFINITE_LOOP_MESS            ("jwizard.message.response.RemoveTrackFromInfiniteLoop"),
    SET_MULTIPLE_REPEATING_TRACK_MESS               ("jwizard.message.response.SetMultipleRepeatingTrack"),
    REMOVE_MULTIPLE_REPEATING_TRACK_MESS            ("jwizard.message.response.RemoveMultipleRepeatingTrack"),
    MULTIPLE_REPEATING_TRACK_INFO_MESS              ("jwizard.message.response.MultipleRepeatingTrackInfo"),
    PAUSE_TRACK_ON_FORCE_MUTE_MESS                  ("jwizard.message.response.PauseTrackOnForceMute"),
    RESUME_TRACK_ON_FORCE_UNMUTE_MESS               ("jwizard.message.response.ResumeTrackOnForceUnmute"),
    PAUSED_TRACK_TIME_MESS                          ("jwizard.message.response.PausedTrackTime"),
    PAUSED_TRACK_ESTIMATE_TIME_MESS                 ("jwizard.message.response.PausedTrackEstimateTime"),
    PAUSED_TRACK_TOTAL_DURATION_MESS                ("jwizard.message.response.PausedTrackTotalDuration"),
    SET_CURRENT_AUDIO_PLAYER_VOLUME_MESS            ("jwizard.message.response.SetCurrentAudioPlayerVolume"),
    GET_CURRENT_AUDIO_PLAYER_VOLUME_MESS            ("jwizard.message.response.GetCurrentAudioPlayerVolume"),
    RESET_AUDIO_PLAYER_VOLUME_MESS                  ("jwizard.message.response.ResetAudioPlayerVolume"),
    CURRENT_PLAYING_TRACK_MESS                      ("jwizard.message.response.CurrentPlayingTrackDesc"),
    CURRENT_PAUSED_TRACK_MESS                       ("jwizard.message.response.CurrentPausedTrackDesc"),
    CURRENT_PLAYING_TIMESTAMP_MESS                  ("jwizard.message.response.CurrentPlayingTimestamp"),
    CURRENT_PAUSED_TIMESTAMP_MESS                   ("jwizard.message.response.CurrentPausedTimestamp"),
    CURRENT_TRACK_LEFT_TO_NEXT_MESS                 ("jwizard.message.response.CurrentTrackLeftToNext"),
    HELP_INFO_SOURCE_CODE_LINK_MESS                 ("jwizard.message.response.HelpInfoSourceCodeLink"),
    HELP_INFO_COMPILATION_VERSION_MESS              ("jwizard.message.response.HelpInfoCompilationVersion"),
    HELP_INFO_COUNT_OF_AVAILABLE_COMMANDS_MESS      ("jwizard.message.response.HelpInfoCountOfAvailableCommands"),
    SKIP_TRACK_AND_PLAY_NEXT_MESS                   ("jwizard.message.response.SkipTrackAndPlayNext"),
    ALL_TRACKS_IN_QUEUE_COUNT_MESS                  ("jwizard.message.response.AllTracksInQueueCount"),
    ALL_TRACKS_IN_QUEUE_DURATION_MESS               ("jwizard.message.response.AllTracksInQueueDuration"),
    APPROX_TO_NEXT_TRACK_FROM_QUEUE_MESS            ("jwizard.message.response.ApproxToNextTrackFromQueue"),
    SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE_MESS      ("jwizard.message.response.SkippedCurrentTrackAndClearQueue"),
    CLEAR_QUEUE_MESS                                ("jwizard.message.response.ClearQueue"),
    QUEUE_WAS_SHUFFLED_MESS                         ("jwizard.message.response.QueueWasShuffled"),
    DEBUG_DATA_MESS                                 ("jwizard.message.response.DebugData"),
    SKIP_TO_SELECT_TRACK_POSITION_MESS              ("jwizard.message.response.SkipToSelectedTrackPosition"),
    REMOVED_TRACKS_FROM_SELECTED_MEMBER_MESS        ("jwizard.message.response.RemoveTracksFromSelectedMember"),
    ADD_PLAYLIST_TO_INFINITE_LOOP_MESS              ("jwizard.message.response.AddPlaylistToInfiniteLoop"),
    REMOVE_PLAYLIST_FROM_INFINITE_LOOP_MESS         ("jwizard.message.response.RemovePlaylistFromInfiniteLoop"),
    TURN_ON_MESS                                    ("jwizard.message.response.TurnOn"),
    TURN_OFF_MESS                                   ("jwizard.message.response.TurnOff"),
    MOVE_BOT_TOO_SELECTED_CHANNEL_MESS              ("jwizard.message.response.MoveBotTooSelectedChannel"),
    MOVE_TRACK_POS_TO_SELECTED_LOCATION_MESS        ("jwizard.message.response.MoveTrackPositionToSelectedLocation"),
    SET_STATS_TO_PUBLIC_MESS                        ("jwizard.message.response.SetStatsToPublic"),
    SET_STATS_TO_PRIVATE_MESS                       ("jwizard.message.response.SetStatsToPrivate"),
    STATS_COLLECTOR_ENABLED_MESS                    ("jwizard.message.response.StatsCollectorEnabled"),
    STATS_COLLECTOR_DISABLED_MESS                   ("jwizard.message.response.StatsCollectorDisabled"),
    MEMBER_STATS_CLEARED_MESS                       ("jwizard.message.response.MemberStatsCleared"),
    GUILD_STATS_CLEARED_MESS                        ("jwizard.message.response.GuildStatsCleared"),
    GUILD_STATS_MODULE_ENABLED_MESS                 ("jwizard.message.response.GuildStatsModuleEnabled"),
    GUILD_STATS_MODULE_DISABLED_MESS                ("jwizard.message.response.GuildStatsModuleDisabled"),
    GUILD_MUSIC_MODULE_ENABLED_MESS                 ("jwizard.message.response.GuildMusicModuleEnabled"),
    GUILD_MUSIC_MODULE_DISABLED_MESS                ("jwizard.message.response.GuildMusicModuleDisabled"),
    GUILD_PLAYLISTS_MODULE_ENABLED_MESS             ("jwizard.message.response.GuildPlaylistsModuleEnabled"),
    GUILD_PLAYLISTS_MODULE_DISABLED_MESS            ("jwizard.message.response.GuildPlaylistsModuleDisabled"),
    GUILD_VOTING_MODULE_ENABLED_MESS                ("jwizard.message.response.GuildVotingModuleEnabled"),
    GUILD_VOTING_MODULE_DISABLED_MESS               ("jwizard.message.response.GuildVotingModuleDisabled"),
    GENERATED_DATE_MESS                             ("jwizard.message.response.GeneratedDate"),
    MESSAGES_SENDED_MESS                            ("jwizard.message.response.MessagesSended"),
    MESSAGES_UPDATED_MESS                           ("jwizard.message.response.MessagesUpdated"),
    REACTIONS_ADDED_MESS                            ("jwizard.message.response.ReactionsAdded"),
    LEVEL_MESS                                      ("jwizard.message.response.Level"),
    SLASH_INTERACTIONS_MESS                         ("jwizard.message.response.SlashInteractions"),
    MESSAGES_DELETED_MESS                           ("jwizard.message.response.MessagesDeleted"),
    REACTIONS_DELETED_MESS                          ("jwizard.message.response.ReactionsDeleted"),
    GUILD_USERS_COUNT_MESS                          ("jwizard.message.response.GuildUsersCount"),
    GUILD_BOTS_COUNT_MESS                           ("jwizard.message.response.GuildBotsCount"),
    GUILD_BOOSTERS_COUNT_MESS                       ("jwizard.message.response.GuildBoostersCount"),
    GUILD_BOOSTING_LEVEL_MESS                       ("jwizard.message.response.GuildBoostingLevel"),

    AUDIO_CHANNEL_WAS_SETTED_MESS                   ("jwizard.message.response.AudioTextChannelWasSetted"),
    AUDIO_CHANNEL_WAS_RESET_MESS                    ("jwizard.message.response.AudioTextChannelWasReset"),
    DJ_ROLE_NAME_WAS_SETTED_MESS                    ("jwizard.message.response.DjRoleNameWasSetted"),
    DJ_ROLE_NAME_WAS_RESET_MESS                     ("jwizard.message.response.DjRoleNameWasReset"),
    I18N_LOCALE_WAS_SETTED_MESS                     ("jwizard.message.response.I18nLocaleWasSetted"),
    I18N_LOCALE_WAS_RESET_MESS                      ("jwizard.message.response.I18nLocaleWasReset"),
    MAX_REPEATS_SINGLE_TRACK_WAS_SETTED_MESS        ("jwizard.message.response.MaxRepeatsSingleTrackWasSetted"),
    MAX_REPEATS_SINGLE_TRACK_WAS_RESET_MESS         ("jwizard.message.response.MaxRepeatsSingleTrackWasReset"),
    PLAYER_DEFAULT_VOLUME_WAS_SETTED_MESS           ("jwizard.message.response.PlayerDefaultVolumeWasSetted"),
    PLAYER_DEFAULT_VOLUME_WAS_RESET_MESS            ("jwizard.message.response.PlayerDefaultVolumeWasReset"),
    SKIP_RATIO_WAS_SETTED_MESS                      ("jwizard.message.response.SkipRatioWasSetted"),
    SKIP_RATIO_WAS_RESET_MESS                       ("jwizard.message.response.SkipRatioWasReset"),
    TIME_TO_END_VOTING_WAS_SETTED_MESS              ("jwizard.message.response.TimeToEndVotingWasSetted"),
    TIME_TO_END_VOTING_WAS_RESET_MESS               ("jwizard.message.response.TimeToEndVotingWasReset"),
    TIME_TO_LEAVE_EMPTY_CHANNEL_WAS_SETTED_MESS     ("jwizard.message.response.TimeToLeaveEmptyChannelWasSetted"),
    TIME_TO_LEAVE_EMPTY_CHANNEL_WAS_RESET_MESS      ("jwizard.message.response.TimeToLeaveEmptyChannelWasReset"),
    TIME_TO_LEAVE_NO_TRACKS_WAS_SETTED_MESS         ("jwizard.message.response.TimeToLeaveNoTracksWasSetted"),
    TIME_TO_LEAVE_NO_TRACKS_WAS_RESET_MESS          ("jwizard.message.response.TimeToLeaveNoTracksWasReset"),

    VOTE_SUFFLE_QUEUE_MESS                          ("jwizard.message.response.VoteShuffleQueue"),
    SUCCESS_VOTE_SUFFLE_QUEUE_MESS                  ("jwizard.message.response.SuccessVoteShuffleQueue"),
    FAILURE_VOTE_SUFFLE_QUEUE_MESS                  ("jwizard.message.response.FailureVoteShuffleQueue"),
    VOTE_SKIP_TRACK_MESS                            ("jwizard.message.response.VoteSkipTrack"),
    SUCCESS_VOTE_SKIP_TRACK_MESS                    ("jwizard.message.response.SuccessVoteSkipTrack"),
    FAILURE_VOTE_SKIP_TRACK_MESS                    ("jwizard.message.response.FailureVoteSkipTrack"),
    VOTE_SKIP_TO_TRACK_MESS                         ("jwizard.message.response.VoteSkipToTrack"),
    SUCCESS_VOTE_SKIP_TO_TRACK_MESS                 ("jwizard.message.response.SuccessVoteSkipToTrack"),
    FAILURE_VOTE_SKIP_TO_TRACK_MESS                 ("jwizard.message.response.FailureVoteSkipToTrack"),
    VOTE_CLEAR_QUEUE_MESS                           ("jwizard.message.response.VoteClearQueue"),
    SUCCESS_VOTE_CLEAR_QUEUE_MESS                   ("jwizard.message.response.SuccessVoteClearQueue"),
    FAILURE_VOTE_CLEAR_QUEUE_MESS                   ("jwizard.message.response.FailureVoteClearQueue"),
    VOTE_STOP_CLEAR_QUEUE_MESS                      ("jwizard.message.response.VoteStopClearQueue"),
    SUCCESS_VOTE_STOP_CLEAR_QUEUE_MESS              ("jwizard.message.response.SuccessVoteStopClearQueue"),
    FAILURE_VOTE_STOP_CLEAR_QUEUE_MESS              ("jwizard.message.response.FailureVoteStopClearQueue");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
