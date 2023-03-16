/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: Utilities.java
 * Last modified: 10/03/2023, 02:15
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

package pl.miloszgilga.misc;

import java.util.concurrent.TimeUnit;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public final class Utilities {

    private static final int MAX_EMBED_PLAYER_INDICATOR_LENGTH = 36;
    private static final char PLAYER_INDICATOR_FULL = '█';
    private static final char PLAYER_INDICATOR_EMPTY = '▒';

    private static final TimeUnit SEC = TimeUnit.SECONDS;
    private static final TimeUnit MILIS = TimeUnit.MILLISECONDS;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Utilities() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String convertMilisToDate(long milis) {
        return String.format("%02d:%02d:%02d", MILIS.toHours(milis),
            MILIS.toMinutes(milis) - TimeUnit.HOURS.toMinutes(MILIS.toHours(milis)),
            MILIS.toSeconds(milis) - TimeUnit.MINUTES.toSeconds(MILIS.toMinutes(milis)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String convertSecondsToMinutes(long seconds) {
        final long minutes = SEC.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(SEC.toHours(seconds));
        if (minutes == 0) {
            return String.format("%ds", SEC.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(SEC.toMinutes(seconds)));
        }
        return String.format("%02dm, %02ds", minutes,
            SEC.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(SEC.toMinutes(seconds)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String createPlayerPercentageTrack(double position, double maxDuration, int maxBlocks) {
        final double progressPerc = position / maxDuration * 100f;
        final int fullBlocksCount = (int) Math.round(maxBlocks * progressPerc / 100);
        final int emptyBlocksCount = maxBlocks - fullBlocksCount;
        return String.valueOf(PLAYER_INDICATOR_FULL).repeat(Math.max(0, fullBlocksCount)) +
            String.valueOf(PLAYER_INDICATOR_EMPTY).repeat(Math.max(0, emptyBlocksCount));
    }

    public static String createPlayerPercentageTrack(double position, double maxDuration) {
        return createPlayerPercentageTrack(position, maxDuration, MAX_EMBED_PLAYER_INDICATOR_LENGTH);
    }
}
