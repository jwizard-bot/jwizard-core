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
    TRACK_QUEUE_IS_EMPTY                            ("jwizard.exception.TrackQueueIsEmptyException"),
    USER_NOT_ADDED_TRACKS_TO_QUEUE                  ("jwizard.exception.UserNotAddedTracksToQueueException");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
