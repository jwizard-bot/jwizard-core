/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmbedColor.java
 * Last modified: 05/03/2023, 23:32
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

package pl.miloszgilga.embed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum EmbedColor {
    GRAY                ("#CFCFCF"),
    PURPLE              ("#8E7CC3"),
    ANTIQUE_WHITE       ("#E0D3C0");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String hex;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Color getColor() {
        return Color.decode(hex);
    }
}
