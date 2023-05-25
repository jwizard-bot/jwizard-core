/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotCommand.java
 * Last modified: 17/05/2023, 01:42
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.miloszgilga;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.StringUtils;

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

    SET_AUDIO_CHANNEL           ("setaudiochn", true, new String[]{ "sadch" },        OTHERS, SET_AUDIO_CHANNEL_COMMAND_DESC,             OPTIONAL_CHANNEL_ID_ARG_SYNTAX),
    SET_DJ_ROLE_NAME            ("setdjrole",   true, new String[]{ "sdjr" },         OTHERS, SET_DJ_ROLE_NAME_COMMAND_DESC,              OPTIONAL_DJ_ROLE_NAME_ARG_SYNTAX),
    SET_I18N_LOCALE             ("setlang",     true, new String[]{ "slng" },         OTHERS, SET_I18N_LOCALE_COMMAND_DESC,               OPTIONAL_I18N_LOCALE_ARG_SYNTAX),
    SET_TRACK_REPEATS           ("settrackrep", true, new String[]{ "strrep" },       OTHERS, SET_TRACK_REPEATS_COMMAND_DESC,             OPTIONAL_TRACK_REPEATS_ARG_SYNTAX),
    SET_DEF_VOLUME              ("setdefvol",   true, new String[]{ "sdfv" },         OTHERS, SET_DEF_VOLUME_COMMAND_DESC,                OPTIONAL_DEFAULT_VOLUME_ARG_SYNTAX),
    SET_SKIP_RATIO              ("setskratio",  true, new String[]{ "ssrt" },         OTHERS, SET_SKIP_RATIO_COMMAND_DESC,                OPTIONAL_SKIP_RATIO_ARG_SYNTAX),
    SET_TIME_VOTING             ("settimevot",  true, new String[]{ "stev" },         OTHERS, SET_TIME_VOTING_COMMAND_DESC,               OPTIONAL_TIME_VOTING_ARG_SYNTAX),
    SET_TIME_LEAVE_EMPTY        ("settleavem",  true, new String[]{ "stlech" },       OTHERS, SET_TIME_LEAVE_EMPTY_COMMAND_DESC,          OPTIONAL_TIME_LEAVE_EMPTY_ARG_SYNTAX),
    SET_TIME_LEAVE_NO_TRACKS    ("settleavetr", true, new String[]{ "stlntch" },      OTHERS, SET_TIME_LEAVE_TO_TRACKS_COMMAND_DESC,      OPTIONAL_TIME_LEAVE_NO_TRACKS_ARG_SYNTAX),

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

    TURN_ON_STATS_MODULE        ("onstatsm",    true, new String[]{ "onstm" },        OWNER,  TURN_ON_STATS_MODULE_COMMAND_DESC),
    TURN_OFF_STATS_MODULE       ("offstatsm",   true, new String[]{ "offstm" },       OWNER,  TURN_OFF_STATS_MODULE_COMMAND_DESC),
    TURN_ON_MUSIC_MODULE        ("onmusicm",    true, new String[]{ "onmsm" },        OWNER,  TURN_ON_MUSIC_MODULE_COMMAND_DESC),
    TURN_OFF_MUSIC_MODULE       ("offmusicm",   true, new String[]{ "offmsm" },       OWNER,  TURN_OFF_MUSIC_MODULE_COMMAND_DESC),
    TURN_ON_PLAYLISTS_MODULE    ("onplaylm",    true, new String[]{ "onplm" },        OWNER,  TURN_ON_PLAYLISTS_MODULE_COMMAND_DESC),
    TURN_OFF_PLAYLISTS_MODULE   ("offplaylm",   true, new String[]{ "offplm" },       OWNER,  TURN_OFF_PLAYLISTS_MODULE_COMMAND_DESC),
    TURN_ON_VOTING_MODULE       ("onvotingm",   true, new String[]{ "onvtm" },        OWNER,  TURN_ON_VOTING_MODULE_COMMAND_DESC),
    TURN_OFF_VOTING_MODULE      ("offvotingm",  true, new String[]{ "offvtm" },       OWNER,  TURN_OFF_VOTING_MODULE_COMMAND_DESC),

    VOTE_SHUFFLE_QUEUE          ("vshuffle",    true, new String[]{ "vshq" },         VOTE,   VOTE_SHUFFLE_QUEUE_COMMAND_DESC),
    VOTE_SKIP_TRACK             ("vskip",       true, new String[]{ "vsk" },          VOTE,   VOTE_SKIP_TRACK_COMMAND_DESC),
    VOTE_SKIP_TO_TRACK          ("vskipto",     true, new String[]{ "vsto" },         VOTE,   VOTE_SKIP_TO_TRACK_COMMAND_DESC,            SKIP_QUEUE_TO_TRACK_ARG_SYNTAX),
    VOTE_CLEAR_QUEUE            ("vclear",      true, new String[]{ "vcl" },          VOTE,   VOTE_CLEAR_QUEUE_COMMAND_DESC),
    VOTE_STOP_CLEAR_QUEUE       ("vstop",       true, new String[]{ "vst" },          VOTE,   VOTE_STOP_CLEAR_QUEUE_COMMAND_DESC);

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

    public static List<String> getCommandsAsEmbedContent(BotConfiguration config, Guild guild) {
        final List<String> commands = new ArrayList<>();
        final String prefix = config.getProperty(BotProperty.J_PREFIX);

        for (final CommandCategory commandCategory : CommandCategory.values()) {
            if (Arrays.stream(values()).noneMatch(v -> v.category.equals(commandCategory))) continue;

            commands.add(String.format("**%s**\n", commandCategory.getHolder(config, guild)).toUpperCase());
            for (final BotCommand command : values()) {
                if (!command.category.equals(commandCategory)) continue;

                final StringBuilder builder = new StringBuilder();
                final String commandAliases = Arrays.stream(command.aliases)
                    .map(a -> prefix + a).collect(Collectors.joining(", "));

                builder.append("`");
                builder.append(prefix);
                builder.append(String.format("%s [%s]", command.name, commandAliases));
                if (!Objects.isNull(command.argSyntax)) {
                    builder.append(String.format(" %s", config.getLocaleText(command.argSyntax, guild)));
                }
                builder.append("`\n");
                builder.append(config.getLocaleText(command.descriptionLocaleSet, guild));
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

    public String prepareArgs(BotConfiguration config) {
        return Objects.isNull(argSyntax) ? StringUtils.EMPTY : argSyntax.parse(config);
    }
}
