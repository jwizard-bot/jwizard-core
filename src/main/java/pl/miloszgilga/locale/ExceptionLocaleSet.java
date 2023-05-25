/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ExceptionLocaleSet.java
 * Last modified: 17/05/2023, 14:32
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
public enum ExceptionLocaleSet implements IEnumerableLocaleSet {

    UNEXPECTED_EXCEPTION                            ("jwizard.exception.UnexpectedException"),
    UNRECOGNIZED_COMMAND                            ("jwizard.exception.UnrecognizedCommandException"),
    USED_COMM_ON_FORBIDDEN_CHANNEL                  ("jwizard.exception.UsedCommandOnForbiddenChannelException"),
    MISMATCH_COMMAND_ARGS_COUNT                     ("jwizard.exception.MismatchCommandArgumentsCountException"),
    UNAUTHORIZED_DJ                                 ("jwizard.exception.UnauthorizedDjException"),
    UNAUTHORIZED_DJ_OR_SENDER                       ("jwizard.exception.UnauthorizedDjOrSenderException"),
    UNAUTHORIZED_MANAGER                            ("jwizard.exception.UnauthorizedManagerException"),
    UNAUTHORIZED_OWNER                              ("jwizard.exception.UnauthorizedOwnerException"),
    USER_NOT_FOUND_IN_GUILD                         ("jwizard.exception.UserNotFoundInGuildException"),
    USER_ID_ALREADY_WITH_BOT                        ("jwizard.exception.UserIsAlreadyWithBotException"),

    CHANNEL_IS_NOT_TEXT_CHANNEL                     ("jwizard.exception.ChannelIsNotTextChannelException"),
    ROLE_ALREADY_EXIST                              ("jwizard.exception.RoleAlreadyExistException"),
    LOCALE_NOT_EXIST                                ("jwizard.exception.LocaleNotFoundException"),
    MAX_REPEATS_OUT_OF_BOUNDS                       ("jwizard.exception.MaxRepeatsOutOfBoundsException"),
    PERCENTAGE_OUT_OF_BOUNDS                        ("jwizard.exception.PercentageOutOfBoundsException"),
    TIME_TO_END_VOTING_OUT_OF_BOUNDS                ("jwizard.exception.TimeToEndVotingOutOfBoundsException"),
    TIME_TO_LEAVE_EMPTY_CHANNEL_OUT_OF_BOUNDS       ("jwizard.exception.TimeToLeaveEmptyChannelOutOfBoundsException"),
    TIME_TO_LEAVE_NO_TRACKS_OUT_OF_BOUNDS           ("jwizard.exception.TimeToLeaveNoTracksOutOfBoundsException"),
    INSUFFICIENT_PERMISSION_ROLE_HIERARCHY          ("jwizard.exception.InsufficientPermissionRoleHierarchyException"),

    STATS_MODULE_IS_ALREADY_RUNNING                 ("jwizard.exception.StatsModuleIsAlreadyRunningException"),
    STATS_MODULE_IS_ALREADY_DISABLED                ("jwizard.exception.StatsModuleIsAlreadyDisabledException"),
    MUSIC_MODULE_IS_ALREADY_RUNNING                 ("jwizard.exception.MusicModuleIsAlreadyRunningException"),
    MUSIC_MODULE_IS_ALREADY_DISABLED                ("jwizard.exception.MusicModuleIsAlreadyDisabledException"),
    PLAYLISTS_MODULE_IS_ALREADY_RUNNING             ("jwizard.exception.PlaylistsModuleIsAlreadyRunningException"),
    PLAYLISTS_MODULE_IS_ALREADY_DISABLED            ("jwizard.exception.PlaylistsModuleIsAlreadyDisabledException"),
    VOTING_MODULE_IS_ALREADY_RUNNING                ("jwizard.exception.VotingModuleIsAlreadyRunningException"),
    VOTING_MODULE_IS_ALREADY_DISABLED               ("jwizard.exception.VotingModuleIsAlreadyDisabledException"),
    STATS_MODULE_IS_TURNED_OFF                      ("jwizard.exception.StatsModuleIsTurnedOffException"),
    MUSIC_MODULE_IS_TURNED_OFF                      ("jwizard.exception.MusicModuleIsTurnedOffException"),
    PLAYLISTS_MODULE_IS_TURNED_OFF                  ("jwizard.exception.PlaylistsModuleIsTurnedOffException"),
    VOTING_MODULE_IS_TURNED_OFF                     ("jwizard.exception.VotingModuleIsTurnedOffException"),

    MEMBER_HAS_NO_STATS_YET_IN_GUILD                ("jwizard.exception.MemberHasNoStatsYetInGuildException"),
    YOU_HAS_NO_STATS_YET_IN_GUILD                   ("jwizard.exception.YouHasNoStatsYetInGuildException"),
    GUILD_HAS_NO_STATS_YET                          ("jwizard.exception.GuildHasNoStatsYetException"),
    STATS_ALREADY_PUBLIC                            ("jwizard.exception.StatsAlreadyPublicException"),
    STATS_ALREADY_PRIVATE                           ("jwizard.exception.StatsAlreadyPrivateException"),
    STATS_ALREADY_ENABLED                           ("jwizard.exception.StatsAlreadyEnabledException"),
    STATS_ALREADY_DISABLED                          ("jwizard.exception.StatsAlreadyDisabledException"),
    YOU_HAS_DISABLED_STATS                          ("jwizard.exception.YouHasDisableStatsException"),
    MEMBER_HAS_DISABLED_STATS                       ("jwizard.exception.MemberHasDisableStatsException"),
    MEMBER_HAS_PRIVATE_STATS                        ("jwizard.exception.MemberHasPrivateStatsException"),

    TRACK_IS_NOT_PLAYING                            ("jwizard.exception.TrackIsNotPlayingException"),
    TRACK_IS_NOT_PAUSED                             ("jwizard.exception.TrackIsNotPausedException"),
    ACTIVE_MUSIC_PLAYING_NOT_FOUND                  ("jwizard.exception.ActiveMusicPlayingNotFoundException"),
    USER_ON_VOICE_CHANNEL_NOT_FOUND                 ("jwizard.exception.UserOnVoiceChannelNotFoundException"),
    USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND        ("jwizard.exception.UserOnVoiceChannelWithBotNotFoundException"),
    INVOKER_IS_NOT_TRACK_SENDER_OR_ADMIN            ("jwizard.exception.InvokerIsNotTrackSenderOrAdminException"),
    TRACK_REPEATS_OUT_OF_BOUNDS                     ("jwizard.exception.TrackRepeatsOutOfBoundsException"),
    LOCK_COMMAND_ON_TEMPORARY_HALTED                ("jwizard.exception.LockCommandOnTemporaryHaltedException"),
    VOLUME_UNITS_OUT_OF_BOUNDS                      ("jwizard.exception.VolumeUnitsOutOfBoundsException"),
    TRACK_OFFSET_OUT_OF_BOUNDS                      ("jwizard.exception.TrackOffsetOutOfBoundsException"),
    TRACK_THE_SAME_POSITION                         ("jwizard.exception.TrackTheSamePositionException"),
    FORBIDDEN_TEXT_CHANNEL                          ("jwizard.exception.ForbiddenTextChannelException"),
    TRACK_QUEUE_IS_EMPTY                            ("jwizard.exception.TrackQueueIsEmptyException"),
    USER_NOT_ADDED_TRACKS_TO_QUEUE                  ("jwizard.exception.UserNotAddedTracksToQueueException");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
