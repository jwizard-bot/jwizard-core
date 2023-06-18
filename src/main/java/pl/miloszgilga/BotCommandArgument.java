/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotCommandArgument.java
 * Last modified: 17/05/2023, 01:20
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

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.core.configuration.BotConfiguration.CAST_TYPES;
import static pl.miloszgilga.exception.CommandException.MismatchCommandArgumentsCountException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotCommandArgument {

    TRACK_LINK_NAME_ARG             (1, BotCommand.PLAY_TRACK,                  String.class,   OptionType.STRING,          "track",         true),
    COUNT_OF_REPEATS                (1, BotCommand.REPEAT_TRACK,                Integer.class,  OptionType.STRING,          "count",         true),
    VOLUME_POINTS                   (1, BotCommand.SET_PLAYER_VOLUME,           Integer.class,  OptionType.INTEGER,         "points",        true),
    REMOVE_TRACK_MEMBER_TAG         (1, BotCommand.REMOVE_MEMBER_TRACKS,        String.class,   OptionType.MENTIONABLE,     "member",        true),
    SKIP_TRACK_POSITION             (1, BotCommand.SKIP_TO_TRACK,               Integer.class,  OptionType.INTEGER,         "pos",           true),
    VOTE_SKIP_TRACK_POSITION        (1, BotCommand.VOTE_SKIP_TO_TRACK,          Integer.class,  OptionType.INTEGER,         "pos",           true),
    MOVE_TRACK_POSITION_FROM        (1, BotCommand.MOVE_TRACK,                  Integer.class,  OptionType.INTEGER,         "frompos",       true),
    MOVE_TRACK_POSITION_TO          (2, BotCommand.MOVE_TRACK,                  Integer.class,  OptionType.INTEGER,         "topos",         true),
    MEMBER_STATS_MEMBER_TAG         (1, BotCommand.MEMBER_STATS,                String.class,   OptionType.MENTIONABLE,     "member",        true),
    RESET_MEMBER_STATS_MEMBER_TAG   (1, BotCommand.RESET_MEMBER_STATS,          String.class,   OptionType.MENTIONABLE,     "member",        true),
    TURN_ON_COMMAND_COMMAND_TAG     (1, BotCommand.TURN_ON_COMMAND,             String.class,   OptionType.STRING,          "command",       true),
    TURN_OFF_COMMAND_COMMAND_TAG    (1, BotCommand.TURN_OFF_COMMAND,            String.class,   OptionType.STRING,          "command",       true),

    SET_AUDIO_TEXT_CHANNEL_TAG      (1, BotCommand.SET_AUDIO_CHANNEL,           String.class,   OptionType.CHANNEL,         "text-channel",  false),
    SET_DJ_ROLE_NAME_TAG            (1, BotCommand.SET_DJ_ROLE_NAME,            String.class,   OptionType.STRING,          "role-name",     false),
    SET_I18N_LOCALE_TAG             (1, BotCommand.SET_I18N_LOCALE,             String.class,   OptionType.STRING,          "lang-code",     false),
    SET_TRACK_REPEATS_TAG           (1, BotCommand.SET_TRACK_REPEATS,           Integer.class,  OptionType.INTEGER,         "repeats",       false),
    SET_DEF_VOLUME_TAG              (1, BotCommand.SET_DEF_VOLUME,              Integer.class,  OptionType.INTEGER,         "volume",        false),
    SET_SKIP_RATIO_TAG              (1, BotCommand.SET_SKIP_RATIO,              Integer.class,  OptionType.INTEGER,         "ratio",         false),
    SET_TIME_VOTING_TAG             (1, BotCommand.SET_TIME_VOTING,             Integer.class,  OptionType.INTEGER,         "seconds",       false),
    SET_TIME_LEAVE_EMPTY_TAG        (1, BotCommand.SET_TIME_LEAVE_EMPTY,        Integer.class,  OptionType.INTEGER,         "seconds",       false),
    SET_TIME_LEAVE_NO_TRACKS_TAG    (1, BotCommand.SET_TIME_LEAVE_NO_TRACKS,    Integer.class,  OptionType.INTEGER,         "seconds",       false),
    SET_TIME_CHOOSE_SONG_TAG        (1, BotCommand.SET_TIME_CHOOSE_SONG,        Integer.class,  OptionType.INTEGER,         "seconds",       false),
    SET_RANDOM_CHOOSE_SONG_TAG      (1, BotCommand.SET_RANDOM_CHOOSE_SONG,      Boolean.class,  OptionType.BOOLEAN,         "logic",         false),
    SET_COUNT_CHOOSE_SONG_TAG       (1, BotCommand.SET_COUNT_CHOOSE_SONG,       Integer.class,  OptionType.INTEGER,         "count",         false);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final int position;
    private final BotCommand command;
    private final Class<?> castingTypeClazz;
    private final OptionType slashOption;
    private final String slashDesc;
    private final boolean isRequired;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<OptionData> fabricateSlashOptions(BotConfiguration config, BotCommand originCmd) {
        if (Objects.isNull(originCmd.getArgSyntax())) return List.of();

        final String syntaxDesc = originCmd.getArgSyntax().parse(config);
        return Arrays.stream(values())
            .filter(v -> v.command.equals(originCmd))
            .map(c -> new OptionData(c.slashOption, c.slashDesc, syntaxDesc, c.isRequired()))
            .toList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Map<BotCommandArgument, String> extractForBaseCommand(
        String input, BotCommand rawCommand, BotConfiguration config, CommandEventWrapper wrapper
    ) {
        final Queue<String> values = new LinkedList<>(Arrays.stream(input.split("\\|")).filter(a -> a.length() > 0).toList());
        final Map<BotCommandArgument, String> extractedArguments = new HashMap<>();
        final List<BotCommandArgument> allBotCommandArguments = getAllArgsForCommand(rawCommand);

        for (BotCommandArgument allBotCommandArgument : allBotCommandArguments) {
            final String value = values.poll();

            if (Objects.isNull(value) && !allBotCommandArgument.isRequired) continue;
            if (Objects.isNull(value)) {
                throw new MismatchCommandArgumentsCountException(config, wrapper, allBotCommandArgument.command);
            }
            extractedArguments.put(allBotCommandArgument, value);
        }
        return extractedArguments;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Map<BotCommandArgument, String> extractForSlashCommand(
        BotConfiguration config, CommandEventWrapper wrapper, List<OptionMapping> options, BotCommand rawCommand
    ) {
        final Map<BotCommandArgument, String> extractedArguments = new HashMap<>();
        final List<BotCommandArgument> allBotCommandArguments = getAllArgsForCommand(rawCommand);
        final Queue<OptionMapping> optionMappingQueue = new LinkedList<>(options);

        for (BotCommandArgument allBotCommandArgument : allBotCommandArguments) {
            final OptionMapping mapping = optionMappingQueue.poll();

            if (Objects.isNull(mapping) && !allBotCommandArgument.isRequired) continue;
            if (Objects.isNull(mapping)) {
                throw new MismatchCommandArgumentsCountException(config, wrapper, allBotCommandArgument.command);
            }
            extractedArguments.put(allBotCommandArgument, mapping.getAsString());
        }
        return extractedArguments;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static List<BotCommandArgument> getAllArgsForCommand(BotCommand command) {
        return Arrays.stream(values())
            .filter(a -> a.command.equals(command))
            .sorted(Comparator.comparingInt(v -> v.position))
            .toList();
    }

    public static int count(BotCommand command) {
        return getAllArgsForCommand(command).size();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    public <T> T parse(Object rawArg) {
        return (T) CAST_TYPES.stream()
            .filter(c -> c.typeClazz().isAssignableFrom(castingTypeClazz))
            .findFirst()
            .map(t -> t.cast().apply(StringUtils.trimToEmpty((String)rawArg)))
            .orElseThrow(() -> new RuntimeException("Unsupported casting type."));
    }
}
