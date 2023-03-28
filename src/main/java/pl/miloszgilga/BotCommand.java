/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotCommand.java
 * Last modified: 23/03/2023, 01:22
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

package pl.miloszgilga;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import pl.miloszgilga.misc.CommandCategory;
import pl.miloszgilga.misc.CommandWithArgsCount;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.core.LocaleSet.*;
import static pl.miloszgilga.misc.CommandCategory.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotCommand {

    HELP                    ("help",        new String[]{ "h", "hl" },      OTHERS, HELP_COMMAND_DESC,                          null,                                 false, 0),
    HELP_ME                 ("helpme",      new String[]{ "hm", "hlm" },    OTHERS, HELPME_COMMAND_DESC,                        null,                                 false, 0),
    DEBUG                   ("debug",       new String[]{ "db", "dbg" },    OTHERS, DEBUG_COMMAND_DESC,                         null,                                 false, 0),

    PLAY_TRACK              ("play",        new String[]{ "p", "pl" },      MUSIC,  PLAY_TRACK_COMMAND_DESC,                    PLAY_TRACK_ARG_SYNTAX,                false, 1),
    PAUSE_TRACK             ("pause",       new String[]{ "ps" },           MUSIC,  PAUSE_TRACK_COMMAND_DESC,                   null,                                 false, 0),
    RESUME_TRACK            ("resume",      new String[]{ "rs" },           MUSIC,  RESUME_TRACK_COMMAND_DESC,                  null,                                 false, 0),
    REPEAT_TRACK            ("repeat",      new String[]{ "rp" },           MUSIC,  REPEAT_TRACK_COMMAND_DESC,                  REPEAT_TRACK_ARG_SYNTAX,              false, 1),
    CLEAR_REPEAT_TRACK      ("repeatcls",   new String[]{ "rpcl" },         MUSIC,  CLEAR_REPEAT_TRACK_COMMAND_DESC,            null,                                 false, 0),
    LOOP_TRACK              ("loop",        new String[]{ "lp" },           MUSIC,  LOOP_TRACK_COMMAND_DESC,                    null,                                 false, 0),
    CURRENT_PLAYING         ("playing",     new String[]{ "cp" },           MUSIC,  CURRENT_PLAYING_TRACK_COMMAND_DESC,         null,                                 false, 0),
    CURRENT_PAUSED          ("paused",      new String[]{ "cps" },          MUSIC,  CURRENT_PAUSED_TRACK_COMMAND_DESC,          null,                                 false, 0),
    GET_PLAYER_VOLUME       ("getvolume",   new String[]{ "gvl" },          MUSIC,  AUDIO_PLAYER_GET_VOLUME_COMMAND_DESC,       null,                                 false, 0),
    QUEUE                   ("queue",       new String[]{ "qt" },           MUSIC,  QUEUE_COMMAND_DESC,                         null,                                 false, 0),

    SET_PLAYER_VOLUME       ("setvolume",   new String[]{ "svl" },          DJ,     AUDIO_PLAYER_SET_VOLUME_COMMAND_DESC,       AUDIO_PLAYER_SET_VOLUME_ARG_SYNTAX,   false, 1),
    RESET_PLAYER_VOLUME     ("volumecls",   new String[]{ "cvl" },          DJ,     AUDIO_PLAYER_RESET_VOLUME_COMMAND_DESC,     null,                                 false, 0),
    JOIN_TO_CHANNEL         ("join",        new String[]{ "jch" },          DJ,     JOIN_TO_CHANNEL_COMMAND_DESC,               null,                                 false, 0),
    REMOVE_MEMBER_TRACKS    ("tracksrm",    new String[]{ "rtr" },          DJ,     REMOVE_MEMBER_TRACKS_COMMAND_DESC,          REMOVE_MEMBER_TRACKS_ARG_SYNTAX,      false, 1),
    SHUFFLE_QUEUE           ("shuffle",     new String[]{ "shq" },          DJ,     SHUFFLE_QUEUE_COMMAND_DESC,                 null,                                 false, 0),
    SKIP_TO_TRACK           ("skipto",      new String[]{ "skt" },          DJ,     SKIP_QUEUE_TO_TRACK_COMMAND_DESC,           SKIP_QUEUE_TO_TRACK_ARG_SYNTAX,       false, 1),
    SKIP_TRACK              ("skip",        new String[]{ "sk" },           DJ,     SKIP_TRACK_COMMAND_DESC,                    null,                                 false, 0),
    STOP_CLEAR_QUEUE        ("stop",        new String[]{ "st" },           DJ,     STOP_CLEAR_QUEUE_COMMAND_DESC,              null,                                 false, 0),
    MOVE_TRACK              ("move",        new String[]{ "mv" },           DJ,     MOVE_TRACK_DESC,                            MOVE_TRACK_ARG_SYNTAX,                false, 1),

    VOTE_SHUFFLE_QUEUE      ("vshuffle",    new String[]{ "vshq" },         MUSIC,  VOTE_SHUFFLE_QUEUE_COMMAND_DESC,            null,                                 false, 0),
    VOTE_SKIP_TRACK         ("vskip",       new String[]{ "vsk" },          MUSIC,  VOTE_SKIP_TRACK_COMMAND_DESC,               null,                                 false, 0),
    VOTE_STOP_CLEAR_QUEUE   ("vstop",       new String[]{ "vst" },          MUSIC,  VOTE_STOP_CLEAR_QUEUE_COMMAND_DESC,         null,                                 false, 0);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final String[] aliases;
    private final CommandCategory category;
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
        final Stream<CommandWithArgsCount> commands =  Arrays.stream(values())
            .map(v -> new CommandWithArgsCount(v.name, v.arguments));
        final Stream<CommandWithArgsCount> aliases = Arrays.stream(values())
            .flatMap(v -> Arrays.stream(v.aliases).map(a -> new CommandWithArgsCount(a, v.arguments)));
        return Stream.concat(commands, aliases).collect(Collectors.toSet());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<String> getCommandsAsEmbedContent(BotConfiguration config) {
        final List<String> commands = new ArrayList<>();
        final String prefix = config.getProperty(BotProperty.J_PREFIX);

        for (final CommandCategory commandCategory : CommandCategory.values()) {
            if (Arrays.stream(values()).noneMatch(v -> v.category.equals(commandCategory))) continue;

            commands.add(String.format("**%s**\n", commandCategory.getHolder(config)).toUpperCase());
            for (final BotCommand command : values()) {
                if (!command.category.equals(commandCategory)) continue;

                final StringBuilder builder = new StringBuilder();
                final String commandAliases = Arrays.stream(command.aliases)
                    .map(a -> prefix + a).collect(Collectors.joining(", "));

                builder.append("`");
                builder.append(prefix);
                builder.append(String.format("%s [%s]", command.name, commandAliases));
                if (!Objects.isNull(command.argSyntax)) {
                    builder.append(String.format(" %s", config.getLocaleText(command.argSyntax)));
                }
                builder.append("`\n");
                builder.append(config.getLocaleText(command.descriptionHolder));
                builder.append('\n');
                commands.add(builder.toString());
            }
        }
        return commands;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String count() {
        return Integer.toString(values().length);
    }
}
