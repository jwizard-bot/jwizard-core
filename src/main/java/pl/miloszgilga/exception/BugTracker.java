/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BugTracker.java
 * Last modified: 23/03/2023, 01:15
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

package pl.miloszgilga.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BugTracker {

    // common
    UNEXPECTED_EXCEPTION                                    (0),
    UNRECOGNIZED_COMMAND                                    (1),
    USED_COMMAND_ON_FORBIDDEN_CHANNEL                       (2),
    MISMATCH_COMMAND_ARGUMENTS                              (3),
    UNAUTHORIZED_DJ                                         (4),
    UNAUTHORIZED_DJ_OR_SENDER                               (5),
    UNAUTHORIZED_MANAGER                                    (6),
    UNAUTHORIZED_OWNER                                      (7),
    USER_NOT_FOUND_IN_GUILD                                 (8),
    USER_ID_ALREADY_WITH_BOT                                (9),

    // music player
    ACTIVE_MUSIC_PLAYING_NOT_FOUND                          (100),
    USER_ON_VOICE_CHANNEL_NOT_FOUND                         (101),
    USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND                (102),
    TRACK_IS_NOT_PLAYING                                    (103),
    TRACK_IS_NOT_PAUSED                                     (104),
    NOT_FOUND_TRACK                                         (105),
    ISSUE_ON_LOAD_TRACK                                     (106),
    ISSUE_WHILE_PLAYING_TRACK                               (107),
    INVOKE_FORBIDDEN_COMMAND                                (108),
    REPEATS_OUT_OF_BOUNDS                                   (109),
    LOCK_COMMAND_TEMPORARY_HALTED                           (110),
    VOLUME_UNITS_OUT_OF_BOUNDS                              (111),
    TRACK_OFFSET_OUT_OF_BOUNDS                              (112),
    TRACK_THE_SAME_POSITIONS                                (113),
    TRACK_QUEUE_IS_EMPTY                                    (114),
    USER_NOT_ADDED_TRACKS_TO_QUEUE                          (115),
    FORBIDDEN_TEXT_CHANNEL                                  (116),

    // statistics
    MEMBER_HAS_NO_STATS_YET                                 (200),
    YOU_HAS_NO_STATS_YET                                    (201),
    GUILD_HAS_NO_STATS_YET                                  (202),
    STATS_ALREADY_PUBLIC                                    (203),
    STATS_ALREADY_PRIVATE                                   (204),
    STATS_ALREADY_ENABLED                                   (205),
    STATS_ALREADY_DISABLED                                  (206),
    YOU_HAS_STATS_DISABLED                                  (207),
    MEMBER_HAS_STATS_DISABLED                               (208),
    MEMBER_HAS_STATS_PRIVATE                                (209),

    // settings
    CHANNEL_IS_NOT_TEXT_CHANNEL                             (300),
    ROLE_ALREADY_EXIST                                      (301),
    LOCALE_NOT_EXIST                                        (302),
    MAX_REPEATS_OUT_OF_BOUNDS                               (303),
    PERCENTAGE_OUT_OF_BOUNDS                                (304),
    TIME_TO_END_VOTING_OUT_OF_BOUNDS                        (305),
    TIME_TO_LEAVE_EMPTY_OUT_OF_BOUNDS                       (306),
    TIME_TO_LEAVE_NO_TRACKS_OUT_OF_BOUNDS                   (307),

    // modules
    STATS_MODULE_IS_ALREADY_RUNNING                         (400),
    STATS_MODULE_IS_ALREADY_DISABLED                        (401),
    MUSIC_MODULE_IS_ALREADY_RUNNING                         (402),
    MUSIC_MODULE_IS_ALREADY_DISABLED                        (403),
    PLAYLISTS_MODULE_IS_ALREADY_RUNNING                     (404),
    PLAYLISTS_MODULE_IS_ALREADY_DISABLED                    (405),
    VOTING_MODULE_IS_ALREADY_RUNNING                        (406),
    VOTING_MODULE_IS_ALREADY_DISABLED                       (407),
    STATS_MODULE_IS_TURNED_OFF                              (408),
    MUSIC_MODULE_IS_TURNED_OFF                              (409),
    PLAYLISTS_MODULE_IS_TURNED_OFF                          (410),
    VOTING_MODULE_IS_TURNED_OFF                             (411);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final int id;
}
