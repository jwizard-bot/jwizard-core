/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandLocaleSet.java
 * Last modified: 16/05/2023, 10:37
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
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
    SET_TIME_CHOOSE_SONG_COMMAND_DESC               ("jwizard.command.description.SetTimeChooseSong"),
    SET_SONG_CHOOSER_RANDOM_COMMAND_DESC            ("jwizard.command.description.SetSongChooserRandomActive"),
    SET_SONG_CHOOSER_COUNT_COMMAND_DESC             ("jwizard.command.description.SetSongChooserCount"),

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
    TURN_ON_COMMAND_COMMAND_DESC                    ("jwizard.command.description.TurnOnCommand"),
    TURN_OFF_COMMAND_COMMAND_DESC                   ("jwizard.command.description.TurnOffCommand"),

    VOTE_SHUFFLE_QUEUE_COMMAND_DESC                 ("jwizard.command.description.VoteShuffleQueue"),
    VOTE_SKIP_TRACK_COMMAND_DESC                    ("jwizard.command.description.VoteSkipTrack"),
    VOTE_SKIP_TO_TRACK_COMMAND_DESC                 ("jwizard.command.description.VoteSkipToTrack"),
    VOTE_CLEAR_QUEUE_COMMAND_DESC                   ("jwizard.command.description.VoteClearQueue"),
    VOTE_STOP_CLEAR_QUEUE_COMMAND_DESC              ("jwizard.command.description.VoteStopClearQueue");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
