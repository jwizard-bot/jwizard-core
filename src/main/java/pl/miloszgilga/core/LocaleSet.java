/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: LocaleSet.java
 * Last modified: 23/02/2023, 16:08
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

package pl.miloszgilga.core;

import lombok.*;
import java.util.Arrays;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum LocaleSet {
    HELP_COMMAND_DESC               ("jwizard.command.description.Help"),
    HELPME_COMMAND_DESC             ("jwizard.command.description.HelpMe");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static LocaleSet findByHolder(String holder) {
        return Arrays.stream(LocaleSet.values())
            .filter(v -> v.holder.equals(holder))
            .findFirst()
            .orElseThrow(() -> { throw new IllegalArgumentException("Holder " + holder + " not exist in lang file"); });
    }
}