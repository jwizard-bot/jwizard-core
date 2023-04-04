/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandException.java
 * Last modified: 23/03/2023, 01:15
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

package pl.miloszgilga.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.locale.ExceptionLocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class CommandException {

    @Slf4j public static class UnrecognizedCommandException extends BotException {
        public UnrecognizedCommandException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.UNRECOGNIZED_COMMAND, Map.of(
                "helpCmd", BotCommand.HELP.parseWithPrefix(config),
                "helpmeCmd", BotCommand.HELP_ME.parseWithPrefix(config)
            ), BugTracker.UNRECOGNIZED_COMMAND);
            JDALog.error(log, event, "Unrecognized command exception. Command not found: %s", event.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UsedCommandOnForbiddenChannelException extends BotException {
        public UsedCommandOnForbiddenChannelException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.USED_COMM_ON_FORBIDDEN_CHANNEL,
                BugTracker.USED_COMMAND_ON_FORBIDDEN_CHANNEL);
            JDALog.error(log, event, "Attempt to invoke command on forbidden channel");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class MismatchCommandArgumentsCountException extends BotException {
        public MismatchCommandArgumentsCountException(BotConfiguration config, CommandEventWrapper event, BotCommand command) {
            super(config, ExceptionLocaleSet.MISMATCH_COMMAND_ARGS_COUNT, Map.of(
                "syntax", command.getAvailableSyntax(config)
            ), BugTracker.MISMATCH_COMMAND_ARGUMENTS);
            JDALog.error(log, event, "Attempt to invoke command on with non-exact arguments count");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnauthorizedDjException extends BotException {
        public UnauthorizedDjException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.UNAUTHORIZED_DJ, BugTracker.UNAUTHORIZED_DJ);
            JDALog.error(log, event, "Attempt to invoke DJ command without DJ guild role");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnauthorizedDjOrSenderException extends BotException {
        public UnauthorizedDjOrSenderException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.UNAUTHORIZED_DJ_OR_SENDER, BugTracker.UNAUTHORIZED_DJ_OR_SENDER);
            JDALog.error(log, event, "Attempt to invoke DJ command without DJ guild role or without send all tracks");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnauthorizedManagerCommandExecutionException extends BotException {
        public UnauthorizedManagerCommandExecutionException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.UNAUTHORIZED_MANAGER, BugTracker.UNAUTHORIZED_MANAGER);
            JDALog.error(log, event, "Attempt to invoke DJ command without DJ guild role");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UserNotFoundInGuildException extends BotException {
        public UserNotFoundInGuildException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.USER_NOT_FOUND_IN_GUILD, BugTracker.USER_NOT_FOUND_IN_GUILD);
            JDALog.error(log, event, "Attempt to find not existing user in selected guild");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UserIsAlreadyWithBotException extends BotException {
        public UserIsAlreadyWithBotException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.USER_ID_ALREADY_WITH_BOT, BugTracker.USER_ID_ALREADY_WITH_BOT);
            JDALog.error(log, event, "Attempt to invoke command, while user is together with bot");
        }
    }
}
