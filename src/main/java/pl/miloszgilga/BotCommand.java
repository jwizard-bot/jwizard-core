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
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.misc.CommandWithArgsCount;

import java.util.Set;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Collectors;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotCommand {
    HELP            ("help",    new String[]{ "h", "hl" },     LocaleSet.HELP_COMMAND_DESC,             null,                               false, 0),
    HELP_ME         ("helpme",  new String[]{ "hm", "hlm" },   LocaleSet.HELPME_COMMAND_DESC,           null,                               false, 0),

    PLAY_TRACK      ("play",    new String[]{ "p", "pl" },     LocaleSet.PLAY_TRACK_COMMAND_DESC,       LocaleSet.PLAY_TRACK_ARG_SYNTAX,    false, 1),
    PAUSE_TRACK     ("pause",   new String[]{ "ps" },          LocaleSet.PAUSE_TRACK_COMMAND_DESC,      null,                               false, 0),
    RESUME_TRACK    ("resume",  new String[]{ "rs" },          LocaleSet.RESUME_TRACK_COMMAND_DESC,     null,                               false, 0),
    REPEAT_TRACK    ("repeat",  new String[]{ "rp" },          LocaleSet.REPEAT_TRACK_COMMAND_DESC,     LocaleSet.REPEAT_TRACK_ARG_SYNTAX,  false, 1),
    LOOP_TRACK      ("loop",    new String[]{ "lp" },          LocaleSet.LOOP_TRACK_COMMAND_DESC,       null,                               false, 0);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final String[] aliases;
    private final LocaleSet descriptionHolder;
    private final LocaleSet argSyntax;
    private final boolean onlyOwner;
    private final int arguments;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getArgSyntax() {
        if (Objects.isNull(argSyntax)) return "";
        return argSyntax.getHolder();
    }

    public String getDescriptionHolder() {
        return descriptionHolder.getHolder();
    }

    public String parseWithPrefix(BotConfiguration config) {
        return "`" + config.getProperty(BotProperty.J_PREFIX) + name + "`";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getAvailableSyntax(BotConfiguration config) {
        final StringBuilder stringBuilder = new StringBuilder();
        final String argLocaleSyntax = config.getLocaleText(argSyntax);
        final String botPrefix = config.getProperty(BotProperty.J_PREFIX);
        stringBuilder.append("\n\n");
        stringBuilder.append(String.format("\t`%s%s %s`", botPrefix, name, argLocaleSyntax));
        for (final String alias : aliases) {
            stringBuilder.append(String.format("\n\t`%s%s %s`", botPrefix, alias, argLocaleSyntax));
        }
        return stringBuilder.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Set<CommandWithArgsCount> getAllCommandsWithAliases() {
        final Stream<CommandWithArgsCount> commands =  Arrays.stream(values()).map(v -> new CommandWithArgsCount(v.name, v.arguments));
        final Stream<CommandWithArgsCount> aliases = Arrays.stream(values())
            .flatMap(v -> Arrays.stream(v.aliases).map(a -> new CommandWithArgsCount(a, v.arguments)));
        return Stream.concat(commands, aliases).collect(Collectors.toSet());
    }
}
