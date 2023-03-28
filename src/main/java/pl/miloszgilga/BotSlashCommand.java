/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotSlashCommand.java
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

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import pl.miloszgilga.slash.SlashOption;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.core.LocaleSet.*;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotSlashCommand {

    HELP                    (BotCommand.HELP),
    HELP_ME                 (BotCommand.HELP_ME),
    DEBUG                   (BotCommand.DEBUG),

    PLAY_TRACK              (BotCommand.PLAY_TRACK,             List.of(new SlashOption(STRING, "track", PLAY_TRACK_ARG_SYNTAX))),
    PAUSE_TRACK             (BotCommand.PAUSE_TRACK),
    RESUME_TRACK            (BotCommand.RESUME_TRACK),
    REPEAT_TRACK            (BotCommand.REPEAT_TRACK,           List.of(new SlashOption(NUMBER, "count", REPEAT_TRACK_ARG_SYNTAX))),
    CLEAR_REPEAT_TRACK      (BotCommand.CLEAR_REPEAT_TRACK),
    LOOP_TRACK              (BotCommand.LOOP_TRACK),
    CURRENT_PLAYING         (BotCommand.CURRENT_PLAYING),
    CURRENT_PAUSED          (BotCommand.CURRENT_PAUSED),
    GET_PLAYER_VOLUME       (BotCommand.GET_PLAYER_VOLUME),
    QUEUE                   (BotCommand.QUEUE),

    SET_PLAYER_VOLUME       (BotCommand.SET_PLAYER_VOLUME,      List.of(new SlashOption(NUMBER, "points", AUDIO_PLAYER_SET_VOLUME_ARG_SYNTAX))),
    RESET_PLAYER_VOLUME     (BotCommand.RESET_PLAYER_VOLUME),
    JOIN_TO_CHANNEL         (BotCommand.JOIN_TO_CHANNEL),
    REMOVE_MEMBER_TRACKS    (BotCommand.REMOVE_MEMBER_TRACKS,   List.of(new SlashOption(MENTIONABLE, "member", REMOVE_MEMBER_TRACKS_ARG_SYNTAX))),
    SHUFFLE_QUEUE           (BotCommand.SHUFFLE_QUEUE),
    SKIP_TO_TRACK           (BotCommand.SKIP_TO_TRACK,          List.of(new SlashOption(NUMBER, "position", SKIP_QUEUE_TO_TRACK_ARG_SYNTAX))),
    SKIP_TRACK              (BotCommand.SKIP_TRACK),
    STOP_CLEAR_QUEUE        (BotCommand.STOP_CLEAR_QUEUE),
    MOVE_TRACK              (BotCommand.MOVE_TRACK,             List.of(new SlashOption(NUMBER, "position", SKIP_QUEUE_TO_TRACK_ARG_SYNTAX))),

    VOTE_SHUFFLE_QUEUE      (BotCommand.VOTE_SHUFFLE_QUEUE),
    VOTE_SKIP_TRACK         (BotCommand.VOTE_SKIP_TRACK),
    VOTE_STOP_CLEAR_QUEUE   (BotCommand.VOTE_STOP_CLEAR_QUEUE);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotCommand regularCommand;
    private final List<SlashOption> slashOptions;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BotSlashCommand(BotCommand regularCommand) {
        this.regularCommand = regularCommand;
        this.slashOptions = new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static BotSlashCommand getFromRegularCommand(BotCommand command) {
        return Arrays.stream(values()).filter(c -> c.regularCommand.equals(command))
            .findFirst()
            .orElseThrow(() -> {
                throw new RuntimeException("Command " + command.getName() + " is not supported by slash command.");
            });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean checkIfSlashExist(String commandName) {
        return Arrays.stream(values()).anyMatch(v -> v.regularCommand.getName().equals(commandName));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<OptionData> fabricateOptions(BotConfiguration config) {
        return slashOptions.stream()
            .map(o -> new OptionData(o.type(), o.key(), config.getLocaleText(o.description()), true)).toList();
    }
}
