/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JCommand.java
 * Last modified: 23/02/2023, 19:10
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

package pl.miloszgilga;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import pl.miloszgilga.core.LocaleSet;

import java.util.Set;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Collectors;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotCommand {
    HELP        ("help",    new String[]{ "h", "hl" },     LocaleSet.HELP_COMMAND_DESC,             false),
    HELP_ME     ("helpme",  new String[]{ "hm", "hlm" },   LocaleSet.HELPME_COMMAND_DESC,           false),
    PLAY_TRACK  ("play",    new String[]{ "p", "pl" },     LocaleSet.PLAY_TRACK_COMMAND_DESC,       false);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final String[] aliases;
    private final LocaleSet descriptionHolder;
    private final boolean onlyOwner;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getDescriptionHolder() {
        return descriptionHolder.getHolder();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Set<String> getAllCommandsWithAliases() {
        final Stream<String> commands =  Arrays.stream(values()).map(v -> v.name);
        final Stream<String> aliases = Arrays.stream(values()).map(v -> v.aliases).flatMap(Arrays::stream);
        return Stream.concat(commands, aliases).collect(Collectors.toSet());
    }
}
