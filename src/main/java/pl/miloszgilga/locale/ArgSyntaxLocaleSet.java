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
public enum ArgSyntaxLocaleSet implements IEnumerableLocaleSet {

    PLAY_TRACK_ARG_SYNTAX                           ("jwizard.command.arguments.PlayTrack"),
    REPEAT_TRACK_ARG_SYNTAX                         ("jwizard.command.arguments.RepeatTrack"),
    AUDIO_PLAYER_SET_VOLUME_ARG_SYNTAX              ("jwizard.command.arguments.AudioPlayerSetVolume"),
    REMOVE_MEMBER_TRACKS_ARG_SYNTAX                 ("jwizard.command.arguments.RemoveMemberTracks"),
    SKIP_QUEUE_TO_TRACK_ARG_SYNTAX                  ("jwizard.command.arguments.SkipQueueToTrack"),
    MOVE_TRACK_ARG_SYNTAX                           ("jwizard.command.arguments.MoveTrack");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
