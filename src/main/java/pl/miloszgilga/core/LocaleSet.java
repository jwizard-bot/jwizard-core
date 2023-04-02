/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: LocaleSet.java
 * Last modified: 23/03/2023, 02:38
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

package pl.miloszgilga.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum LocaleSet {
    HELP_COMMAND_DESC                               ("jwizard.command.description.Help"),
    HELPME_COMMAND_DESC                             ("jwizard.command.description.HelpMe"),
    DEBUG_COMMAND_DESC                              ("jwizard.command.description.Debug"),
    PLAY_TRACK_COMMAND_DESC                         ("jwizard.command.description.PlayTrack"),
    PAUSE_TRACK_COMMAND_DESC                        ("jwizard.command.description.PauseTrack"),
    RESUME_TRACK_COMMAND_DESC                       ("jwizard.command.description.ResumeTrack"),
    REPEAT_TRACK_COMMAND_DESC                       ("jwizard.command.description.RepeatTrack"),
    CLEAR_REPEAT_TRACK_COMMAND_DESC                 ("jwizard.command.description.ClearRepeatTrack"),
    LOOP_TRACK_COMMAND_DESC                         ("jwizard.command.description.LoopTrack"),
    CURRENT_PLAYING_TRACK_COMMAND_DESC              ("jwizard.command.description.CurrentPlaying"),
    CURRENT_PAUSED_TRACK_COMMAND_DESC               ("jwizard.command.description.CurrentPaused"),
    AUDIO_PLAYER_SET_VOLUME_COMMAND_DESC            ("jwizard.command.description.AudioPlayerSetVolume"),
    AUDIO_PLAYER_GET_VOLUME_COMMAND_DESC            ("jwizard.command.description.AudioPlayerGetVolume"),
    AUDIO_PLAYER_RESET_VOLUME_COMMAND_DESC          ("jwizard.command.description.AudioPlayerResetVolume"),
    QUEUE_COMMAND_DESC                              ("jwizard.command.description.Queue"),
    JOIN_TO_CHANNEL_COMMAND_DESC                    ("jwizard.command.description.JoinToChannel"),
    REMOVE_MEMBER_TRACKS_COMMAND_DESC               ("jwizard.command.description.RemoveMemberTracks"),
    SHUFFLE_QUEUE_COMMAND_DESC                      ("jwizard.command.description.ShuffleQueue"),
    SKIP_QUEUE_TO_TRACK_COMMAND_DESC                ("jwizard.command.description.SkipQueueToTrack"),
    SKIP_TRACK_COMMAND_DESC                         ("jwizard.command.description.SkipTrack"),
    STOP_CLEAR_QUEUE_COMMAND_DESC                   ("jwizard.command.description.StopClearQueue"),
    MOVE_TRACK_DESC                                 ("jwizard.command.description.MoveTrack"),
    INFINITE_PLAYLIST_DESC                          ("jwizard.command.description.InfinitePlaylist"),
    VOTE_SHUFFLE_QUEUE_COMMAND_DESC                 ("jwizard.command.description.VoteShuffleQueue"),
    VOTE_SKIP_TRACK_COMMAND_DESC                    ("jwizard.command.description.VoteSkipTrack"),
    VOTE_STOP_CLEAR_QUEUE_COMMAND_DESC              ("jwizard.command.description.VoteStopClearQueue"),

    PLAY_TRACK_ARG_SYNTAX                           ("jwizard.command.arguments.PlayTrack"),
    REPEAT_TRACK_ARG_SYNTAX                         ("jwizard.command.arguments.RepeatTrack"),
    AUDIO_PLAYER_SET_VOLUME_ARG_SYNTAX              ("jwizard.command.arguments.AudioPlayerSetVolume"),
    REMOVE_MEMBER_TRACKS_ARG_SYNTAX                 ("jwizard.command.arguments.RemoveMemberTracks"),
    SKIP_QUEUE_TO_TRACK_ARG_SYNTAX                  ("jwizard.command.arguments.SkipQueueToTrack"),
    MOVE_TRACK_ARG_SYNTAX                           ("jwizard.command.arguments.MoveTrack"),

    COMMAND_CATEGORY_MUSIC                          ("jwizard.command.category.Music"),
    COMMAND_CATEGORY_DJ_ROLE                        ("jwizard.command.category.DjRole"),
    COMMAND_CATEGORY_STATISTICS                     ("jwizard.command.category.Statistics"),
    COMMAND_CATEGORY_OWNER_AND_MANAGER              ("jwizard.command.category.OwnerAndManager"),
    COMMAND_CATEGORY_OTHERS                         ("jwizard.command.category.Others"),

    INFO_HEADER                                     ("jwizard.message.response.header.info"),
    WARN_HEADER                                     ("jwizard.message.response.header.warn"),
    ERROR_HEADER                                    ("jwizard.message.response.header.error"),

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

    GENERAL_HEADER_DEBUG                            ("jwizard.message.debug.header.General"),
    CONFIGURATION_HEADER_DEBUG                      ("jwizard.message.debug.header.Configuration"),
    VERSIONS_HEADER_DEBUG                           ("jwizard.message.debug.header.Versions"),
    JVM_HEADER_DEBUG                                ("jwizard.message.debug.header.JavaVirtualMachine"),

    JVM_NAME_JAVA_DEBUG                             ("jwizard.message.debug.java.JVMName"),
    JVM_VERSION_JAVA_DEBUG                          ("jwizard.message.debug.java.JVMVersion"),
    JVM_SPEC_VERSION_JAVA_DEBUG                     ("jwizard.message.debug.java.JVMSpecVersion"),
    JRE_NAME_JAVA_DEBUG                             ("jwizard.message.debug.java.JREName"),
    JRE_VERSION_JAVA_DEBUG                          ("jwizard.message.debug.java.JREVersion"),
    JRE_SPEC_VERSION_JAVA_DEBUG                     ("jwizard.message.debug.java.JRESpecVersion"),
    OS_NAME_JAVA_DEBUG                              ("jwizard.message.debug.java.OSName"),
    OS_ARCHITECTURE_JAVA_DEBUG                      ("jwizard.message.debug.java.OSArchitecture"),

    BOT_VERSION_DEBUG                               ("jwizard.message.debug.BotVersion"),
    BOT_LOCALE_DEBUG                                ("jwizard.message.debug.BotLocale"),
    CURRENT_GUILD_OWNER_TAG_DEBUG                   ("jwizard.message.debug.CurrentGuildOwnerTag"),
    CURRENT_GUILD_ID_DEBUG                          ("jwizard.message.debug.CurrentGuildId"),
    DEFAULT_PREFIX_DEBUG                            ("jwizard.message.debug.DefaultPrefix"),
    ENABLE_SLASH_COMMANDS_DEBUG                     ("jwizard.message.debug.EnableSlashCommands"),
    VOTE_MAX_WAITING_TIME_DEBUG                     ("jwizard.message.debug.VoteMaxWaitingTime"),
    LEAVE_CHANNEL_WAITING_TIME_DEBUG                ("jwizard.message.debug.LeaveChannelWaitingTime"),
    JDA_VERSION_DEBUG                               ("jwizard.message.debug.JdaVersion"),
    JDA_UTILITIES_VERSION_DEBUG                     ("jwizard.message.debug.JdaUtilitiesVersion"),
    LAVAPLAYER_VERSION_DEBUG                        ("jwizard.message.debug.LavaplayerVersion"),
    JVM_XMX_MEMORY_DEBUG                            ("jwizard.message.debug.JVMXmxMemory"),
    JVM_USED_MEMORY_DEBUG                           ("jwizard.message.debug.JVMUsedMemory"),

    TRACK_IS_NOT_PLAYING_EXC                        ("jwizard.exception.TrackIsNotPlayingException"),
    TRACK_IS_NOT_PAUSED_EXC                         ("jwizard.exception.TrackIsNotPausedException"),
    ACTIVE_MUSIC_PLAYING_NOT_FOUND_EXC              ("jwizard.exception.ActiveMusicPlayingNotFoundException"),
    USER_ON_VOICE_CHANNEL_NOT_FOUND_EXEC            ("jwizard.exception.UserOnVoiceChannelNotFoundException"),
    USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND_EXEC   ("jwizard.exception.UserOnVoiceChannelWithBotNotFoundException"),
    UNRECOGNIZED_COMMAND_EXC                        ("jwizard.exception.UnrecognizedCommandException"),
    USED_COMM_ON_FORBIDDEN_CHANNEL_EXC              ("jwizard.exception.UsedCommandOnForbiddenChannelException"),
    MISMATCH_COMMAND_ARGS_COUNT_EXC                 ("jwizard.exception.MismatchCommandArgumentsCountException"),
    INVOKER_IS_NOT_TRACK_SENDER_OR_ADMIN_EXC        ("jwizard.exception.InvokerIsNotTrackSenderOrAdminException"),
    TRACK_REPEATS_OUT_OF_BOUNDS_EXC                 ("jwizard.exception.TrackRepeatsOutOfBoundsException"),
    LOCK_COMMAND_ON_TEMPORARY_HALTED_EXC            ("jwizard.exception.LockCommandOnTemporaryHaltedException"),
    VOLUME_UNITS_OUT_OF_BOUNDS_EXC                  ("jwizard.exception.VolumeUnitsOutOfBoundsException"),
    TRACK_OFFSET_OUT_OF_BOUNDS_EXC                  ("jwizard.exception.TrackOffsetOutOfBoundsException"),
    TRACK_THE_SAME_POSITION_EXC                     ("jwizard.exception.TrackTheSamePositionException"),
    UNAUTHORIZED_DJ_EXC                             ("jwizard.exception.UnauthorizedDjException"),
    UNAUTHORIZED_DJ_OR_SENDER_EXC                   ("jwizard.exception.UnauthorizedDjOrSenderException"),
    UNAUTHORIZED_MANAGER_EXC                        ("jwizard.exception.UnauthorizedManagerException"),
    TRACK_QUEUE_IS_EMPTY_EXC                        ("jwizard.exception.TrackQueueIsEmptyException"),
    USER_NOT_FOUND_IN_GUILD_EXC                     ("jwizard.exception.UserNotFoundInGuildException"),
    USER_NOT_ADDED_TRACKS_TO_QUEUE_EXC              ("jwizard.exception.UserNotAddedTracksToQueueException"),
    USER_ID_ALREADY_WITH_BOT_EXC                    ("jwizard.exception.UserIsAlreadyWithBotException");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static LocaleSet findByHolder(String holder) {
        return Arrays.stream(LocaleSet.values())
            .filter(v -> v.holder.equals(holder))
            .findFirst()
            .orElseThrow(() -> { throw new IllegalArgumentException("Holder " + holder + " not exist in lang file"); });
    }
}
