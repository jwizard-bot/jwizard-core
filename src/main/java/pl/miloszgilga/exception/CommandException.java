/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandException.java
 * Last modified: 16/05/2023, 19:00
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

package pl.miloszgilga.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.locale.ExceptionLocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class CommandException {

    @Slf4j public static class UnrecognizedCommandException extends BotException {
        public UnrecognizedCommandException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.UNRECOGNIZED_COMMAND, Map.of(
                "helpCmd", BotCommand.HELP.parseWithPrefix(config),
                "helpmeCmd", BotCommand.HELP_ME.parseWithPrefix(config)
            ), BugTracker.UNRECOGNIZED_COMMAND);
            JDALog.error(log, event, "Unrecognized command exception. Command not found: %s", event.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UsedCommandOnForbiddenChannelException extends BotException {
        public UsedCommandOnForbiddenChannelException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.USED_COMM_ON_FORBIDDEN_CHANNEL,
                BugTracker.USED_COMMAND_ON_FORBIDDEN_CHANNEL);
            JDALog.error(log, event, "Attempt to invoke command on forbidden channel");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class MismatchCommandArgumentsCountException extends BotException {
        public MismatchCommandArgumentsCountException(BotConfiguration config, CommandEventWrapper event, BotCommand command) {
            super(config, event.getGuild(), ExceptionLocaleSet.MISMATCH_COMMAND_ARGS_COUNT, Map.of(
                "syntax", command.getAvailableSyntax(config)
            ), BugTracker.MISMATCH_COMMAND_ARGUMENTS);
            JDALog.error(log, event, "Attempt to invoke command on with non-exact arguments count");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnauthorizedDjException extends BotException {
        public UnauthorizedDjException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.UNAUTHORIZED_DJ, BugTracker.UNAUTHORIZED_DJ);
            JDALog.error(log, event, "Attempt to invoke DJ command without DJ guild role");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnauthorizedDjOrSenderException extends BotException {
        public UnauthorizedDjOrSenderException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.UNAUTHORIZED_DJ_OR_SENDER, BugTracker.UNAUTHORIZED_DJ_OR_SENDER);
            JDALog.error(log, event, "Attempt to invoke DJ command without DJ guild role or without send all tracks");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnauthorizedManagerCommandExecutionException extends BotException {
        public UnauthorizedManagerCommandExecutionException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.UNAUTHORIZED_MANAGER, BugTracker.UNAUTHORIZED_MANAGER);
            JDALog.error(log, event, "Attempt to invoke MANAGER role command without MANAGER guild role");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnauthorizedOwnerCommandExecutionException extends BotException {
        public UnauthorizedOwnerCommandExecutionException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.UNAUTHORIZED_OWNER, BugTracker.UNAUTHORIZED_OWNER);
            JDALog.error(log, event, "Attempt to invoke OWNER role command without OWNER guild role");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UserNotFoundInGuildException extends BotException {
        public UserNotFoundInGuildException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.USER_NOT_FOUND_IN_GUILD, BugTracker.USER_NOT_FOUND_IN_GUILD);
            JDALog.error(log, event, "Attempt to find not existing user in selected guild");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UserIsAlreadyWithBotException extends BotException {
        public UserIsAlreadyWithBotException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.USER_ID_ALREADY_WITH_BOT, BugTracker.USER_ID_ALREADY_WITH_BOT);
            JDALog.error(log, event, "Attempt to invoke command, while user is together with bot");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UnexpectedException extends BotException {
        public UnexpectedException(BotConfiguration config, CommandEventWrapper event) {
            super(config, Objects.isNull(event) ? null : event.getGuild(), ExceptionLocaleSet.UNEXPECTED_EXCEPTION, Map.of(
                "helpEmail", Utilities.getRichEmailLink(config.getProperty(BotProperty.J_HELP_EMAIL))
            ), BugTracker.UNEXPECTED_EXCEPTION);
            if (Objects.isNull(event)) {
                log.error("Unexpected exception during executable.");
            } else {
                JDALog.error(log, event, "Unexpected exception during executable.");
            }
        }
    }
}
