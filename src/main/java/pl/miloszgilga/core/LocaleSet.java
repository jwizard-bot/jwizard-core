/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: LocaleSet.java
 * Last modified: 23/02/2023, 16:08
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum LocaleSet {
    HELP_COMMAND_DESC                           ("jwizard.command.description.Help"),
    HELPME_COMMAND_DESC                         ("jwizard.command.description.HelpMe"),
    PLAY_TRACK_COMMAND_DESC                     ("jwizard.command.description.PlayTrack"),
    PAUSE_TRACK_COMMAND_DESC                    ("jwizard.command.description.PauseTrack"),
    RESUME_TRACK_COMMAND_DESC                   ("jwizard.command.description.ResumeTrack"),
    REPEAT_TRACK_COMMAND_DESC                   ("jwizard.command.description.RepeatTrack"),
    CLEAR_REPEAT_TRACK_COMMAND_DESC             ("jwizard.command.description.ClearRepeatTrack"),
    LOOP_TRACK_COMMAND_DESC                     ("jwizard.command.description.LoopTrack"),
    CURRENT_PLAYING_TRACK_DESC                  ("jwizard.command.description.CurrentPlaying"),
    AUDIO_PLAYER_SET_VOLUME_DESC                ("jwizard.command.description.AudioPlayerSetVolume"),
    AUDIO_PLAYER_GET_VOLUME_DESC                ("jwizard.command.description.AudioPlayerGetVolume"),
    AUDIO_PLAYER_RESET_VOLUME_DESC              ("jwizard.command.description.AudioPlayerResetVolume"),

    PLAY_TRACK_ARG_SYNTAX                       ("jwizard.command.arguments.PlayTrack"),
    REPEAT_TRACK_ARG_SYNTAX                     ("jwizard.command.arguments.RepeatTrack"),
    AUDIO_PLAYER_SET_VOLUME_ARG_SYNTAX          ("jwizard.command.arguments.AudioPlayerSetVolume"),

    INFO_HEADER                                 ("jwizard.message.response.header.info"),
    WARN_HEADER                                 ("jwizard.message.response.header.warn"),
    ERROR_HEADER                                ("jwizard.message.response.header.error"),

    ADD_NEW_TRACK_MESS                          ("jwizard.message.response.AddNewTrack"),
    TRACK_NAME_MESS                             ("jwizard.message.response.TrackName"),
    TRACK_DURATION_TIME_MESS                    ("jwizard.message.response.DurationTime"),
    TRACK_POSITION_IN_QUEUE_MESS                ("jwizard.message.response.PositionInQueue"),
    ADD_NEW_PLAYLIST_MESS                       ("jwizard.message.response.AddNewPlaylist"),
    COUNT_OF_TRACKS_MESS                        ("jwizard.message.response.CountOfTracks"),
    TRACKS_TOTAL_DURATION_TIME_MESS             ("jwizard.message.response.TotalDurationTime"),
    TRACK_ADDDED_BY_MESS                        ("jwizard.message.response.AddedBy"),
    NEXT_TRACK_INDEX_MESS                       ("jwizard.message.response.NextTrackIndex"),
    NOT_FOUND_TRACK_MESS                        ("jwizard.message.response.NotFoundAudioTrack"),
    ISSUE_WHILE_LOADING_TRACK_MESS              ("jwizard.message.response.UnexpectedErrorOnLoadTrack"),
    ISSUE_WHILE_PLAYING_TRACK_MESS              ("jwizard.message.response.UnexpectedErrorOnPlayTrack"),
    BUG_TRACKER_MESS                            ("jwizard.message.response.BugTracker"),
    PAUSE_TRACK_MESS                            ("jwizard.message.response.PauseTrack"),
    RESUME_TRACK_MESS                           ("jwizard.message.response.ResumeTrack"),
    LEAVE_EMPTY_CHANNEL_MESS                    ("jwizard.message.response.LeaveEmptyChannel"),
    ON_TRACK_START_MESS                         ("jwizard.message.response.OnTrackStart"),
    ON_TRACK_START_ON_PAUSED_MESS               ("jwizard.message.response.OnTrackStartOnPause"),
    ON_END_PLAYBACK_QUEUE_MESS                  ("jwizard.message.response.OnEndPlaybackQueue"),
    LEAVE_END_PLAYBACK_QUEUE_MESS               ("jwizard.message.response.LeaveEndPlaybackQueue"),
    ADD_TRACK_TO_INFINITE_LOOP_MESS             ("jwizard.message.response.AddTrackToInfiniteLoop"),
    REMOVE_TRACK_FROM_INFINITE_LOOP_MESS        ("jwizard.message.response.RemoveTrackFromInfiniteLoop"),
    SET_MULTIPLE_REPEATING_TRACK_MESS           ("jwizard.message.response.SetMultipleRepeatingTrack"),
    REMOVE_MULTIPLE_REPEATING_TRACK_MESS        ("jwizard.message.response.RemoveMultipleRepeatingTrack"),
    MULTIPLE_REPEATING_TRACK_INFO_MESS          ("jwizard.message.response.MultipleRepeatingTrackInfo"),
    PAUSE_TRACK_ON_FORCE_MUTE_MESS              ("jwizard.message.response.PauseTrackOnForceMute"),
    RESUME_TRACK_ON_FORCE_UNMUTE_MESS           ("jwizard.message.response.ResumeTrackOnForceUnmute"),
    PAUSED_TRACK_TIME_MESS                      ("jwizard.message.response.PausedTrackTime"),
    PAUSED_TRACK_ESTIMATE_TIME_MESS             ("jwizard.message.response.PausedTrackEstimateTime"),
    PAUSED_TRACK_TOTAL_DURATION_MESS            ("jwizard.message.response.PausedTrackTotalDuration"),
    SET_CURRENT_AUDIO_PLAYER_VOLUME_MESS        ("jwizard.message.response.SetCurrentAudioPlayerVolume"),
    GET_CURRENT_AUDIO_PLAYER_VOLUME_MESS        ("jwizard.message.response.GetCurrentAudioPlayerVolume"),
    RESET_AUDIO_PLAYER_VOLUME_MESS              ("jwizard.message.response.ResetAudioPlayerVolume"),

    TRACK_IS_NOT_PLAYING_EXC                    ("jwizard.exception.TrackIsNotPlayingException"),
    TRACK_IS_NOT_PAUSED_EXC                     ("jwizard.exception.TrackIsNotPausedException"),
    ACTIVE_MUSIC_PLAYING_NOT_FOUND_EXC          ("jwizard.exception.ActiveMusicPlayingNotFoundException"),
    USER_ON_VOICE_CHANNEL_NOT_FOUND_EXEC        ("jwizard.exception.UserOnVoiceChannelNotFoundException"),
    UNRECOGNIZED_COMMAND_EXC                    ("jwizard.exception.UnrecognizedCommandException"),
    USED_COMM_ON_FORBIDDEN_CHANNEL_EXC          ("jwizard.exception.UsedCommandOnForbiddenChannelException"),
    MISMATCH_COMMAND_ARGS_COUNT_EXC             ("jwizard.exception.MismatchCommandArgumentsCountException"),
    INVOKER_IS_NOT_TRACK_SENDER_OR_ADMIN_EXC    ("jwizard.exception.InvokerIsNotTrackSenderOrAdminException"),
    TRACK_REPEATS_OUT_OF_BOUNDS_EXC             ("jwizard.exception.TrackRepeatsOutOfBoundsException"),
    LOCK_COMMAND_ON_TEMPORARY_HALTED_EXC        ("jwizard.exception.LockCommandOnTemporaryHaltedException"),
    VOLUME_UNITS_OUT_OF_BOUNDS_EXC              ("jwizard.exception.VolumeUnitsOutOfBoundsException"),
    UNAUTHORIZED_DJ_COMMAND_EXECUTION_EXC       ("jwizard.exception.UnauthorizedDjCommandExecutionException");

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
