/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IPlayerManager.java
 * Last modified: 04/03/2023, 23:39
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

package pl.miloszgilga.audioplayer;

import com.jagrosh.jdautilities.command.CommandEvent;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

interface IPlayerManager {
    void loadAndPlay(CommandEvent event, String trackUrl, boolean isUrlPattern);
    void pauseCurrentTrack(CommandEvent event);
    void resumeCurrentTrack(CommandEvent event);
    void repeatCurrentTrack(CommandEvent event, int countOfRepeats);
    boolean toggleInfiniteLoopCurrentTrack(CommandEvent event);
    void setPlayerVolume(CommandEvent event, int volume);
}
