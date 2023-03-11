/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BugTracker.java
 * Last modified: 11/03/2023, 09:52
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

    ACTIVE_MUSIC_PLAYING_NOT_FOUND                          (101),
    USER_ON_VOICE_CHANNEL_NOT_FOUND                         (102),
    TRACK_IS_NOT_PLAYING                                    (103),
    TRACK_IS_NOT_PAUSED                                     (104),
    NOT_FOUND_TRACK                                         (105),
    ISSUE_ON_LOAD_TRACK                                     (106),
    ISSUE_WHILE_PLAYING_TRACK                               (107),
    INVOKE_FORBIDDEN_COMMAND                                (108);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final int id;
}
