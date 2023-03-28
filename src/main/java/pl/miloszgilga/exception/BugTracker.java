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
    UNRECOGNIZED_COMMAND                                    (1),
    USED_COMMAND_ON_FORBIDDEN_CHANNEL                       (2),
    MISMATCH_COMMAND_ARGUMENTS                              (3),
    UNAUTHORIZED_DJ_COMMAND_EXECUTION                       (4),
    UNAUTHORIZED_MANAGER_COMMAND_EXECUTION                  (5),
    USER_NOT_FOUND_IN_GUILD                                 (6),

    ACTIVE_MUSIC_PLAYING_NOT_FOUND                          (101),
    USER_ON_VOICE_CHANNEL_NOT_FOUND                         (102),
    USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND                (103),
    TRACK_IS_NOT_PLAYING                                    (104),
    TRACK_IS_NOT_PAUSED                                     (105),
    NOT_FOUND_TRACK                                         (106),
    ISSUE_ON_LOAD_TRACK                                     (107),
    ISSUE_WHILE_PLAYING_TRACK                               (108),
    INVOKE_FORBIDDEN_COMMAND                                (109),
    REPEATS_OUT_OF_BOUNDS                                   (110),
    LOCK_COMMAND_TEMPORARY_HALTED                           (111),
    VOLUME_UNITS_OUT_OF_BOUNDS                              (112),
    TRACK_OFFSET_OUT_OF_BOUNDS                              (113),
    TRACK_QUEUE_IS_EMPTY                                    (114),
    USER_NOT_ADDED_TRACKS_TO_QUEUE                          (115);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final int id;
}
