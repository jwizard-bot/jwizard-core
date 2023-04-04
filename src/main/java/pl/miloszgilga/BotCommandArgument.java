/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandArgument.java
 * Last modified: 03/04/2023, 02:21
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

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.exception.CommandException;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.core.configuration.BotConfiguration.CAST_TYPES;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotCommandArgument {
    TRACK_LINK_NAME_ARG             (1, BotCommand.PLAY_TRACK,             String.class,   OptionType.STRING,          "track",     true),
    COUNT_OF_REPEATS                (1, BotCommand.REPEAT_TRACK,           Integer.class,  OptionType.STRING,          "count",     true),
    VOLUME_POINTS                   (1, BotCommand.SET_PLAYER_VOLUME,      Integer.class,  OptionType.INTEGER,         "points",    true),
    MEMBER_TAG                      (1, BotCommand.REMOVE_MEMBER_TRACKS,   String.class,   OptionType.MENTIONABLE,     "member",    true),
    SKIP_TRACK_POSITION             (1, BotCommand.SKIP_TO_TRACK,          Integer.class,  OptionType.INTEGER,         "pos",       true),
    VOTE_SKIP_TRACK_POSITION        (1, BotCommand.VOTE_SKIP_TO_TRACK,     Integer.class,  OptionType.INTEGER,         "pos",       true),
    MOVE_TRACK_POSITION_FROM        (1, BotCommand.MOVE_TRACK,             Integer.class,  OptionType.INTEGER,         "frompos",   true),
    MOVE_TRACK_POSITION_TO          (2, BotCommand.MOVE_TRACK,             Integer.class,  OptionType.INTEGER,         "topos",     true);

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
        final String syntaxDesc = config.getLocaleText(originCmd.getArgSyntax());
        return Arrays.stream(values())
            .filter(v -> v.command.equals(originCmd))
            .map(c -> new OptionData(c.slashOption, c.slashDesc, syntaxDesc, c.isRequired()))
            .toList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Map<BotCommandArgument, String> extractForBaseCommand(
        String input, BotCommand rawCommand, BotConfiguration config, CommandEventWrapper wrapper
    ) {
        final List<String> values = Arrays.stream(input.split("\\|")).filter(a -> a.length() > 0).toList();
        final Map<BotCommandArgument, String> extractedArguments = new HashMap<>();
        final List<BotCommandArgument> allBotCommandArguments = getAllArgsForCommand(rawCommand);

        if (values.size() != allBotCommandArguments.size()) {
            throw new CommandException.MismatchCommandArgumentsCountException(config, wrapper, rawCommand);
        }
        for (int i = 0; i < values.size(); i++) {
            extractedArguments.put(allBotCommandArguments.get(i), values.get(i));
        }
        return extractedArguments;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Map<BotCommandArgument, String> extractForSlashCommand(List<OptionMapping> options, BotCommand rawCommand) {
        final Map<BotCommandArgument, String> extractedArguments = new HashMap<>();
        final List<BotCommandArgument> allBotCommandArguments = getAllArgsForCommand(rawCommand);

        for (int i = 0; i < allBotCommandArguments.size(); i++) {
            extractedArguments.put(allBotCommandArguments.get(i), options.get(i).getAsString());
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
