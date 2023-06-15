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

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.CommandWithProxyDto;
import pl.miloszgilga.misc.CommandCategory;
import pl.miloszgilga.misc.CommandWithArgsCount;
import pl.miloszgilga.locale.CommandLocaleSet;
import pl.miloszgilga.locale.ArgSyntaxLocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.misc.CommandCategory.*;
import static pl.miloszgilga.locale.CommandLocaleSet.*;
import static pl.miloszgilga.locale.ArgSyntaxLocaleSet.*;
import static pl.miloszgilga.exception.CommandStateException.FollowedCommandArgumentNotExistException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotCommand {

    HELP                        ("help",        true, List.of("h", "hl"),      OTHERS, HELP_COMMAND_DESC),
    HELP_ME                     ("helpme",      true, List.of("hm", "hlm"),    OTHERS, HELPME_COMMAND_DESC),
    DEBUG                       ("debug",       true, List.of("db", "dbg"),    OTHERS, DEBUG_COMMAND_DESC),

    SET_AUDIO_CHANNEL           ("setaudiochn", true, List.of("sadch"),        OTHERS, SET_AUDIO_CHANNEL_COMMAND_DESC,             OPTIONAL_CHANNEL_ID_ARG_SYNTAX),
    SET_DJ_ROLE_NAME            ("setdjrole",   true, List.of("sdjr"),         OTHERS, SET_DJ_ROLE_NAME_COMMAND_DESC,              OPTIONAL_DJ_ROLE_NAME_ARG_SYNTAX),
    SET_I18N_LOCALE             ("setlang",     true, List.of("slng"),         OTHERS, SET_I18N_LOCALE_COMMAND_DESC,               OPTIONAL_I18N_LOCALE_ARG_SYNTAX),
    SET_TRACK_REPEATS           ("settrackrep", true, List.of("strrep"),       OTHERS, SET_TRACK_REPEATS_COMMAND_DESC,             OPTIONAL_TRACK_REPEATS_ARG_SYNTAX),
    SET_DEF_VOLUME              ("setdefvol",   true, List.of("sdfv"),         OTHERS, SET_DEF_VOLUME_COMMAND_DESC,                OPTIONAL_DEFAULT_VOLUME_ARG_SYNTAX),
    SET_SKIP_RATIO              ("setskratio",  true, List.of("ssrt"),         OTHERS, SET_SKIP_RATIO_COMMAND_DESC,                OPTIONAL_SKIP_RATIO_ARG_SYNTAX),
    SET_TIME_VOTING             ("settimevot",  true, List.of("stev"),         OTHERS, SET_TIME_VOTING_COMMAND_DESC,               OPTIONAL_TIME_VOTING_ARG_SYNTAX),
    SET_TIME_LEAVE_EMPTY        ("settleavem",  true, List.of("stlech"),       OTHERS, SET_TIME_LEAVE_EMPTY_COMMAND_DESC,          OPTIONAL_TIME_LEAVE_EMPTY_ARG_SYNTAX),
    SET_TIME_LEAVE_NO_TRACKS    ("settleavetr", true, List.of("stlntch"),      OTHERS, SET_TIME_LEAVE_TO_TRACKS_COMMAND_DESC,      OPTIONAL_TIME_LEAVE_NO_TRACKS_ARG_SYNTAX),

    PLAY_TRACK                  ("play",        true, List.of("p", "pl"),      MUSIC,  PLAY_TRACK_COMMAND_DESC,                    PLAY_TRACK_ARG_SYNTAX),
    PAUSE_TRACK                 ("pause",       true, List.of("ps"),           MUSIC,  PAUSE_TRACK_COMMAND_DESC),
    RESUME_TRACK                ("resume",      true, List.of("rs"),           MUSIC,  RESUME_TRACK_COMMAND_DESC),
    REPEAT_TRACK                ("repeat",      true, List.of("rp"),           MUSIC,  REPEAT_TRACK_COMMAND_DESC,                  REPEAT_TRACK_ARG_SYNTAX),
    CLEAR_REPEAT_TRACK          ("repeatcls",   true, List.of("rpcl"),         MUSIC,  CLEAR_REPEAT_TRACK_COMMAND_DESC),
    LOOP_TRACK                  ("loop",        true, List.of("lp"),           MUSIC,  LOOP_TRACK_COMMAND_DESC),
    CURRENT_PLAYING             ("playing",     true, List.of("cp"),           MUSIC,  CURRENT_PLAYING_TRACK_COMMAND_DESC),
    CURRENT_PAUSED              ("paused",      true, List.of("cps"),          MUSIC,  CURRENT_PAUSED_TRACK_COMMAND_DESC),
    GET_PLAYER_VOLUME           ("getvolume",   true, List.of("gvl"),          MUSIC,  AUDIO_PLAYER_GET_VOLUME_COMMAND_DESC),
    QUEUE                       ("queue",       true, List.of("qt"),           MUSIC,  QUEUE_COMMAND_DESC),

    SET_PLAYER_VOLUME           ("setvolume",   true, List.of("svl"),          DJ,     AUDIO_PLAYER_SET_VOLUME_COMMAND_DESC,       AUDIO_PLAYER_SET_VOLUME_ARG_SYNTAX),
    RESET_PLAYER_VOLUME         ("volumecls",   true, List.of("cvl"),          DJ,     AUDIO_PLAYER_RESET_VOLUME_COMMAND_DESC),
    JOIN_TO_CHANNEL             ("join",        true, List.of("jch"),          DJ,     JOIN_TO_CHANNEL_COMMAND_DESC),
    REMOVE_MEMBER_TRACKS        ("tracksrm",    true, List.of("rtr"),          DJ,     REMOVE_MEMBER_TRACKS_COMMAND_DESC,          MEMBER_TAG_ARG_SYNTAX),
    SHUFFLE_QUEUE               ("shuffle",     true, List.of("shq"),          DJ,     SHUFFLE_QUEUE_COMMAND_DESC),
    SKIP_TO_TRACK               ("skipto",      true, List.of("skt"),          DJ,     SKIP_QUEUE_TO_TRACK_COMMAND_DESC,           SKIP_QUEUE_TO_TRACK_ARG_SYNTAX),
    SKIP_TRACK                  ("skip",        true, List.of("sk"),           DJ,     SKIP_TRACK_COMMAND_DESC),
    CLEAR_QUEUE                 ("clear",       true, List.of("cl"),           DJ,     CLEAR_QUEUE_COMMAND_DESC),
    STOP_CLEAR_QUEUE            ("stop",        true, List.of("st"),           DJ,     STOP_CLEAR_QUEUE_COMMAND_DESC),
    MOVE_TRACK                  ("move",        true, List.of("mv"),           DJ,     MOVE_TRACK_COMMAND_DESC,                    MOVE_TRACK_ARG_SYNTAX),
    INFINITE_PLAYLIST           ("infinite",    true, List.of("inf"),          DJ,     INFINITE_PLAYLIST_COMMAND_DESC),

    GUILD_STATS                 ("gstats",      true, List.of("mst"),          STATS,  GUILD_STATS_COMMAND_DESC),
    MEMBER_STATS                ("mstats",      true, List.of("gst"),          STATS,  MEMBER_STATS_COMMAND_DESC,                  MEMBER_TAG_ARG_SYNTAX),
    MY_STATS                    ("mystats",     true, List.of("myst"),         STATS,  MY_STATS_COMMAND_DESC),
    ENABLE_STATS                ("statson",     true, List.of("ston"),         STATS,  ENABLE_STATS_COMMAND_DESC),
    DISABLE_STATS               ("statsoff",    true, List.of("stoff"),        STATS,  DISABLE_STATS_COMMAND_DESC),
    PUBLIC_STATS                ("pubstats",    true, List.of("pubst"),        STATS,  PUBLIC_STATS_COMMAND_DESC),
    PRIVATE_STATS               ("privstats",   true, List.of("privst"),       STATS,  PRIVATE_STATS_COMMAND_DESC),
    RESET_MEMBER_STATS          ("resetmstats", true, List.of("rsmst"),        STATS,  RESET_MEMBER_STATS_COMMAND_DESC,            MEMBER_TAG_ARG_SYNTAX),
    RESET_GUILD_STATS           ("resetgstats", true, List.of("rsgst"),        STATS,  RESET_GUILD_STATS_COMMAND_DESC),

    TURN_ON_STATS_MODULE        ("onstatsm",    true, List.of("onstm"),        OWNER,  TURN_ON_STATS_MODULE_COMMAND_DESC),
    TURN_OFF_STATS_MODULE       ("offstatsm",   true, List.of("offstm"),       OWNER,  TURN_OFF_STATS_MODULE_COMMAND_DESC),
    TURN_ON_MUSIC_MODULE        ("onmusicm",    true, List.of("onmsm"),        OWNER,  TURN_ON_MUSIC_MODULE_COMMAND_DESC),
    TURN_OFF_MUSIC_MODULE       ("offmusicm",   true, List.of("offmsm"),       OWNER,  TURN_OFF_MUSIC_MODULE_COMMAND_DESC),
    TURN_ON_PLAYLISTS_MODULE    ("onplaylm",    true, List.of("onplm"),        OWNER,  TURN_ON_PLAYLISTS_MODULE_COMMAND_DESC),
    TURN_OFF_PLAYLISTS_MODULE   ("offplaylm",   true, List.of("offplm"),       OWNER,  TURN_OFF_PLAYLISTS_MODULE_COMMAND_DESC),
    TURN_ON_VOTING_MODULE       ("onvotingm",   true, List.of("onvtm"),        OWNER,  TURN_ON_VOTING_MODULE_COMMAND_DESC),
    TURN_OFF_VOTING_MODULE      ("offvotingm",  true, List.of("offvtm"),       OWNER,  TURN_OFF_VOTING_MODULE_COMMAND_DESC),
    TURN_ON_COMMAND             ("commandon",   true, List.of("cmdon"),        OWNER,  TURN_ON_COMMAND_COMMAND_DESC,               COMMAND_NAME_OR_ALIAS_ARG_SYNTAX),
    TURN_OFF_COMMAND            ("commandoff",  true, List.of("cmdoff"),       OWNER,  TURN_OFF_COMMAND_COMMAND_DESC,              COMMAND_NAME_OR_ALIAS_ARG_SYNTAX),

    VOTE_SHUFFLE_QUEUE          ("vshuffle",    true, List.of("vshq"),         VOTE,   VOTE_SHUFFLE_QUEUE_COMMAND_DESC),
    VOTE_SKIP_TRACK             ("vskip",       true, List.of("vsk"),          VOTE,   VOTE_SKIP_TRACK_COMMAND_DESC),
    VOTE_SKIP_TO_TRACK          ("vskipto",     true, List.of("vsto"),         VOTE,   VOTE_SKIP_TO_TRACK_COMMAND_DESC,            SKIP_QUEUE_TO_TRACK_ARG_SYNTAX),
    VOTE_CLEAR_QUEUE            ("vclear",      true, List.of("vcl"),          VOTE,   VOTE_CLEAR_QUEUE_COMMAND_DESC),
    VOTE_STOP_CLEAR_QUEUE       ("vstop",       true, List.of("vst"),          VOTE,   VOTE_STOP_CLEAR_QUEUE_COMMAND_DESC);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final boolean slashActive;
    private final List<String> aliases;
    private final CommandCategory category;
    private final CommandLocaleSet descriptionLocaleSet;
    private final ArgSyntaxLocaleSet argSyntax;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BotCommand(String name, boolean slashActive, List<String> aliases, CommandCategory category, CommandLocaleSet descriptionLocaleSet) {
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
            .flatMap(v -> v.aliases.stream().map(a -> new CommandWithArgsCount(a, BotCommandArgument.count(v))));
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
                final String commandAliases = command.aliases.stream()
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

    public static CommandWithProxyDto getCategoryFromRawCommand(
        String cmdNameOrAlias, BotConfiguration config, CommandEventWrapper event
    ) {
        return Arrays.stream(values())
            .filter(c -> c.name.equalsIgnoreCase(cmdNameOrAlias) || c.aliases.contains(cmdNameOrAlias.toLowerCase()))
            .findFirst()
            .map(c -> new CommandWithProxyDto(c, c.category))
            .orElseThrow(() -> new FollowedCommandArgumentNotExistException(config, event, cmdNameOrAlias));
    }

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
