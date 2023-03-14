/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandException.java
 * Last modified: 05/03/2023, 23:09
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

package pl.miloszgilga.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class CommandException {

    @Slf4j public static class UnrecognizedCommandException extends BotException {
        public UnrecognizedCommandException(BotConfiguration config, EventWrapper event) {
            super(config, LocaleSet.UNRECOGNIZED_COMMAND_EXC, Map.of(
                "helpCmd", BotCommand.HELP.parseWithPrefix(config),
                "helpmeCmd", BotCommand.HELP_ME.parseWithPrefix(config)
            ), BugTracker.UNRECOGNIZED_COMMAND);
            log.debug("G: {}, A: {} <> Unrecognized command exception. Command not found: {}",
                event.guildName(), event.authorTag(), event.message());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UsedCommandOnForbiddenChannelException extends BotException {
        public UsedCommandOnForbiddenChannelException(BotConfiguration config, EventWrapper event) {
            super(config, LocaleSet.USED_COMM_ON_FORBIDDEN_CHANNEL_EXC,
                BugTracker.USED_COMMAND_ON_FORBIDDEN_CHANNEL);
            log.error("G: {}, A: {} <> Attempt to invoke command on forbidden channel",
                event.guildName(), event.authorTag());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class MismatchCommandArgumentsCountException extends BotException {
        public MismatchCommandArgumentsCountException(BotConfiguration config, EventWrapper event, BotCommand command) {
            super(config, LocaleSet.MISMATCH_COMMAND_ARGS_COUNT_EXC, Map.of(
                "syntax", command.getAvailableSyntax(config)
            ), BugTracker.MISMATCH_COMMAND_ARGUMENTS);
            log.error("G: {}, A: {} <> Attempt to invoke command on with non-exact arguments count",
                event.guildName(), event.authorTag());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnauthorizedDjCommandExecutionException extends BotException {
        public UnauthorizedDjCommandExecutionException(BotConfiguration config, EventWrapper event) {
            super(config, LocaleSet.UNAUTHORIZED_DJ_COMMAND_EXECUTION_EXC, BugTracker.UNAUTHORIZED_DJ_COMMAND_EXECUTION);
            log.error("G: {}, A: {} <> Attempt to invoke DJ command without DJ guild role",
                event.guildName(), event.authorTag());
        }
    }
}
