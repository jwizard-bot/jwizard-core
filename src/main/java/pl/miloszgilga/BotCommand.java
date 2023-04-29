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
import pl.miloszgilga.locale.CommandLocaleSet;
import pl.miloszgilga.locale.ArgSyntaxLocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.misc.CommandCategory.*;
import static pl.miloszgilga.locale.CommandLocaleSet.*;
import static pl.miloszgilga.locale.ArgSyntaxLocaleSet.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotCommand {

    HELP                        ("help",        true, new String[]{ "h", "hl" },      OTHERS, HELP_COMMAND_DESC),
    HELP_ME                     ("helpme",      true, new String[]{ "hm", "hlm" },    OTHERS, HELPME_COMMAND_DESC),
    DEBUG                       ("debug",       true, new String[]{ "db", "dbg" },    OTHERS, DEBUG_COMMAND_DESC),

    PLAY_TRACK                  ("play",        true, new String[]{ "p", "pl" },      MUSIC,  PLAY_TRACK_COMMAND_DESC,                    PLAY_TRACK_ARG_SYNTAX),
    PAUSE_TRACK                 ("pause",       true, new String[]{ "ps" },           MUSIC,  PAUSE_TRACK_COMMAND_DESC),
    RESUME_TRACK                ("resume",      true, new String[]{ "rs" },           MUSIC,  RESUME_TRACK_COMMAND_DESC),
    REPEAT_TRACK                ("repeat",      true, new String[]{ "rp" },           MUSIC,  REPEAT_TRACK_COMMAND_DESC,                  REPEAT_TRACK_ARG_SYNTAX),
    CLEAR_REPEAT_TRACK          ("repeatcls",   true, new String[]{ "rpcl" },         MUSIC,  CLEAR_REPEAT_TRACK_COMMAND_DESC),
    LOOP_TRACK                  ("loop",        true, new String[]{ "lp" },           MUSIC,  LOOP_TRACK_COMMAND_DESC),
    CURRENT_PLAYING             ("playing",     true, new String[]{ "cp" },           MUSIC,  CURRENT_PLAYING_TRACK_COMMAND_DESC),
    CURRENT_PAUSED              ("paused",      true, new String[]{ "cps" },          MUSIC,  CURRENT_PAUSED_TRACK_COMMAND_DESC),
    GET_PLAYER_VOLUME           ("getvolume",   true, new String[]{ "gvl" },          MUSIC,  AUDIO_PLAYER_GET_VOLUME_COMMAND_DESC),
    QUEUE                       ("queue",       true, new String[]{ "qt" },           MUSIC,  QUEUE_COMMAND_DESC),

    SET_PLAYER_VOLUME           ("setvolume",   true, new String[]{ "svl" },          DJ,     AUDIO_PLAYER_SET_VOLUME_COMMAND_DESC,       AUDIO_PLAYER_SET_VOLUME_ARG_SYNTAX),
    RESET_PLAYER_VOLUME         ("volumecls",   true, new String[]{ "cvl" },          DJ,     AUDIO_PLAYER_RESET_VOLUME_COMMAND_DESC),
    JOIN_TO_CHANNEL             ("join",        true, new String[]{ "jch" },          DJ,     JOIN_TO_CHANNEL_COMMAND_DESC),
    REMOVE_MEMBER_TRACKS        ("tracksrm",    true, new String[]{ "rtr" },          DJ,     REMOVE_MEMBER_TRACKS_COMMAND_DESC,          MEMBER_TAG_ARG_SYNTAX),
    SHUFFLE_QUEUE               ("shuffle",     true, new String[]{ "shq" },          DJ,     SHUFFLE_QUEUE_COMMAND_DESC),
    SKIP_TO_TRACK               ("skipto",      true, new String[]{ "skt" },          DJ,     SKIP_QUEUE_TO_TRACK_COMMAND_DESC,           SKIP_QUEUE_TO_TRACK_ARG_SYNTAX),
    SKIP_TRACK                  ("skip",        true, new String[]{ "sk" },           DJ,     SKIP_TRACK_COMMAND_DESC),
    CLEAR_QUEUE                 ("clear",       true, new String[]{ "cl" },           DJ,     CLEAR_QUEUE_COMMAND_DESC),
    STOP_CLEAR_QUEUE            ("stop",        true, new String[]{ "st" },           DJ,     STOP_CLEAR_QUEUE_COMMAND_DESC),
    MOVE_TRACK                  ("move",        true, new String[]{ "mv" },           DJ,     MOVE_TRACK_COMMAND_DESC,                    MOVE_TRACK_ARG_SYNTAX),
    INFINITE_PLAYLIST           ("infinite",    true, new String[]{ "inf" },          DJ,     INFINITE_PLAYLIST_COMMAND_DESC),

    GUILD_STATS                 ("gstats",      true, new String[]{ "mst" },          STATS,  GUILD_STATS_COMMAND_DESC),
    MEMBER_STATS                ("mstats",      true, new String[]{ "gst" },          STATS,  MEMBER_STATS_COMMAND_DESC,                  MEMBER_TAG_ARG_SYNTAX),
    MY_STATS                    ("mystats",     true, new String[]{ "myst" },         STATS,  MY_STATS_COMMAND_DESC),
    ENABLE_STATS                ("statson",     true, new String[]{ "ston" },         STATS,  ENABLE_STATS_COMMAND_DESC),
    DISABLE_STATS               ("statsoff",    true, new String[]{ "stoff" },        STATS,  DISABLE_STATS_COMMAND_DESC),
    PUBLIC_STATS                ("pubstats",    true, new String[]{ "pubst" },        STATS,  PUBLIC_STATS_COMMAND_DESC),
    PRIVATE_STATS               ("privstats",   true, new String[]{ "privst" },       STATS,  PRIVATE_STATS_COMMAND_DESC),
    RESET_MEMBER_STATS          ("resetmstats", true, new String[]{ "rsmst" },        STATS,  RESET_MEMBER_STATS_COMMAND_DESC,            MEMBER_TAG_ARG_SYNTAX),
    RESET_GUILD_STATS           ("resetgstats", true, new String[]{ "rsgst" },        STATS,  RESET_GUILD_STATS_COMMAND_DESC),

    VOTE_SHUFFLE_QUEUE      ("vshuffle",    true, new String[]{ "vshq" },         MUSIC,  VOTE_SHUFFLE_QUEUE_COMMAND_DESC),
    VOTE_SKIP_TRACK         ("vskip",       true, new String[]{ "vsk" },          MUSIC,  VOTE_SKIP_TRACK_COMMAND_DESC),
    VOTE_SKIP_TO_TRACK      ("vskipto",     true, new String[]{ "vsto" },         MUSIC,  VOTE_SKIP_TO_TRACK_COMMAND_DESC,            SKIP_QUEUE_TO_TRACK_ARG_SYNTAX),
    VOTE_CLEAR_QUEUE        ("vclear",      true, new String[]{ "vcl" },          MUSIC,  VOTE_CLEAR_QUEUE_COMMAND_DESC),
    VOTE_STOP_CLEAR_QUEUE   ("vstop",       true, new String[]{ "vst" },          MUSIC,  VOTE_STOP_CLEAR_QUEUE_COMMAND_DESC);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final boolean slashActive;
    private final String[] aliases;
    private final CommandCategory category;
    private final CommandLocaleSet descriptionLocaleSet;
    private final ArgSyntaxLocaleSet argSyntax;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BotCommand(String name, boolean slashActive, String[] aliases, CommandCategory category, CommandLocaleSet descriptionLocaleSet) {
        this.name = name;
        this.slashActive = slashActive;
        this.aliases = aliases;
        this.category = category;
        this.descriptionLocaleSet = descriptionLocaleSet;
        this.argSyntax = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandLocaleSet getDescriptionLocaleSet() {
        return descriptionLocaleSet;
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
            .map(v -> new CommandWithArgsCount(v.name, BotCommandArgument.count(v)));
        final Stream<CommandWithArgsCount> aliases = Arrays.stream(values())
            .flatMap(v -> Arrays.stream(v.aliases).map(a -> new CommandWithArgsCount(a, BotCommandArgument.count(v))));
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
                builder.append(config.getLocaleText(command.descriptionLocaleSet));
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

    public static boolean checkIfSlashExist(String commandName) {
        return Arrays.stream(values()).anyMatch(v -> v.getName().equals(commandName) && v.slashActive);
    }
}
