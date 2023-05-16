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
public enum CommandLocaleSet implements IEnumerableLocaleSet {

    HELP_COMMAND_DESC                               ("jwizard.command.description.Help"),
    HELPME_COMMAND_DESC                             ("jwizard.command.description.HelpMe"),
    DEBUG_COMMAND_DESC                              ("jwizard.command.description.Debug"),
    SET_AUDIO_CHANNEL_COMMAND_DESC                  ("jwizard.command.description.SetAudioChannel"),
    SET_DJ_ROLE_NAME_COMMAND_DESC                   ("jwizard.command.description.SetDjRoleName"),
    SET_I18N_LOCALE_COMMAND_DESC                    ("jwizard.command.description.SetI18nLocale"),
    SET_TRACK_REPEATS_COMMAND_DESC                  ("jwizard.command.description.SetTrackRepeats"),
    SET_DEF_VOLUME_COMMAND_DESC                     ("jwizard.command.description.SetDefaultVolume"),
    SET_SKIP_RATIO_COMMAND_DESC                     ("jwizard.command.description.SetSkipRatio"),
    SET_TIME_VOTING_COMMAND_DESC                    ("jwizard.command.description.SetTimeVoting"),
    SET_TIME_LEAVE_EMPTY_COMMAND_DESC               ("jwizard.command.description.SetTimeToLeaveEmptyChannel"),
    SET_TIME_LEAVE_TO_TRACKS_COMMAND_DESC           ("jwizard.command.description.SetTimeToLeaveNoTracksChannel"),

    PLAY_TRACK_COMMAND_DESC                         ("jwizard.command.description.PlayTrack"),
    PAUSE_TRACK_COMMAND_DESC                        ("jwizard.command.description.PauseTrack"),
    RESUME_TRACK_COMMAND_DESC                       ("jwizard.command.description.ResumeTrack"),
    REPEAT_TRACK_COMMAND_DESC                       ("jwizard.command.description.RepeatTrack"),
    CLEAR_REPEAT_TRACK_COMMAND_DESC                 ("jwizard.command.description.ClearRepeatTrack"),
    LOOP_TRACK_COMMAND_DESC                         ("jwizard.command.description.LoopTrack"),
    CURRENT_PLAYING_TRACK_COMMAND_DESC              ("jwizard.command.description.CurrentPlaying"),
    CURRENT_PAUSED_TRACK_COMMAND_DESC               ("jwizard.command.description.CurrentPaused"),
    AUDIO_PLAYER_GET_VOLUME_COMMAND_DESC            ("jwizard.command.description.AudioPlayerGetVolume"),
    QUEUE_COMMAND_DESC                              ("jwizard.command.description.Queue"),

    AUDIO_PLAYER_SET_VOLUME_COMMAND_DESC            ("jwizard.command.description.AudioPlayerSetVolume"),
    AUDIO_PLAYER_RESET_VOLUME_COMMAND_DESC          ("jwizard.command.description.AudioPlayerResetVolume"),
    JOIN_TO_CHANNEL_COMMAND_DESC                    ("jwizard.command.description.JoinToChannel"),
    REMOVE_MEMBER_TRACKS_COMMAND_DESC               ("jwizard.command.description.RemoveMemberTracks"),
    SHUFFLE_QUEUE_COMMAND_DESC                      ("jwizard.command.description.ShuffleQueue"),
    SKIP_QUEUE_TO_TRACK_COMMAND_DESC                ("jwizard.command.description.SkipQueueToTrack"),
    SKIP_TRACK_COMMAND_DESC                         ("jwizard.command.description.SkipTrack"),
    CLEAR_QUEUE_COMMAND_DESC                        ("jwizard.command.description.ClearQueue"),
    STOP_CLEAR_QUEUE_COMMAND_DESC                   ("jwizard.command.description.StopClearQueue"),
    MOVE_TRACK_COMMAND_DESC                         ("jwizard.command.description.MoveTrack"),
    INFINITE_PLAYLIST_COMMAND_DESC                  ("jwizard.command.description.InfinitePlaylist"),

    GUILD_STATS_COMMAND_DESC                        ("jwizard.command.description.GuildStats"),
    MEMBER_STATS_COMMAND_DESC                       ("jwizard.command.description.MemberStats"),
    MY_STATS_COMMAND_DESC                           ("jwizard.command.description.MyStats"),
    ENABLE_STATS_COMMAND_DESC                       ("jwizard.command.description.EnableStats"),
    DISABLE_STATS_COMMAND_DESC                      ("jwizard.command.description.DisableStats"),
    PUBLIC_STATS_COMMAND_DESC                       ("jwizard.command.description.PublicStats"),
    PRIVATE_STATS_COMMAND_DESC                      ("jwizard.command.description.PrivateStats"),
    RESET_MEMBER_STATS_COMMAND_DESC                 ("jwizard.command.description.ResetMemberStats"),
    RESET_GUILD_STATS_COMMAND_DESC                  ("jwizard.command.description.ResetGuildStats"),

    TURN_ON_STATS_MODULE_COMMAND_DESC               ("jwizard.command.description.TurnOnStats"),
    TURN_OFF_STATS_MODULE_COMMAND_DESC              ("jwizard.command.description.TurnOffStats"),
    TURN_ON_MUSIC_MODULE_COMMAND_DESC               ("jwizard.command.description.TurnOnMusic"),
    TURN_OFF_MUSIC_MODULE_COMMAND_DESC              ("jwizard.command.description.TurnOffMusic"),
    TURN_ON_PLAYLISTS_MODULE_COMMAND_DESC           ("jwizard.command.description.TurnOnPlaylists"),
    TURN_OFF_PLAYLISTS_MODULE_COMMAND_DESC          ("jwizard.command.description.TurnOffPlaylists"),
    TURN_ON_VOTING_MODULE_COMMAND_DESC              ("jwizard.command.description.TurnOnVoting"),
    TURN_OFF_VOTING_MODULE_COMMAND_DESC             ("jwizard.command.description.TurnOffVoting"),

    VOTE_SHUFFLE_QUEUE_COMMAND_DESC                 ("jwizard.command.description.VoteShuffleQueue"),
    VOTE_SKIP_TRACK_COMMAND_DESC                    ("jwizard.command.description.VoteSkipTrack"),
    VOTE_SKIP_TO_TRACK_COMMAND_DESC                 ("jwizard.command.description.VoteSkipToTrack"),
    VOTE_CLEAR_QUEUE_COMMAND_DESC                   ("jwizard.command.description.VoteClearQueue"),
    VOTE_STOP_CLEAR_QUEUE_COMMAND_DESC              ("jwizard.command.description.VoteStopClearQueue");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
