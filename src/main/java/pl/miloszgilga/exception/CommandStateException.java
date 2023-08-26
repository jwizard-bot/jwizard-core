/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandStateException.java
 * Last modified: 6/15/23, 5:05 PM
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

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.locale.ExceptionLocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class CommandStateException {

    @Slf4j public static class CommandIsAlreadyTurnedOnException extends BotException {
        public CommandIsAlreadyTurnedOnException(BotConfiguration config, CommandEventWrapper event, BotCommand command) {
            super(config, event.getGuild(), ExceptionLocaleSet.COMMAND_IS_ALREADY_TURNED_ON, Map.of(
                "command", command.parseWithPrefix(config),
                "turnOffCmd", BotCommand.TURN_OFF_COMMAND.parseWithPrefix(config, command.getName())
            ), BugTracker.COMMAND_IS_ALREADY_TURNED_ON);
            JDALog.error(log, event, "Attempt to turn on already turned on command. Command: {}", command.getName());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class CommandIsAlreadyTurnedOffException extends BotException {
        public CommandIsAlreadyTurnedOffException(BotConfiguration config, CommandEventWrapper event, BotCommand command) {
            super(config, event.getGuild(), ExceptionLocaleSet.COMMAND_IS_ALREADY_TURNED_OFF, Map.of(
                "command", command.parseWithPrefix(config),
                "turnOnCmd", BotCommand.TURN_ON_COMMAND.parseWithPrefix(config, command.getName())
            ), BugTracker.COMMAND_IS_ALREADY_TURNED_OFF);
            JDALog.error(log, event, "Attempt to turn off already turned off command. Command: {}", command.getName());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class CommandIsTurnedOffException extends BotException {
        public CommandIsTurnedOffException(BotConfiguration config, CommandEventWrapper event, BotCommand command) {
            super(config, event.getGuild(), ExceptionLocaleSet.COMMAND_IS_TURNED_OFF, Map.of(
                "command", command.parseWithPrefix(config)
            ), BugTracker.COMMAND_IS_TURNED_OFF);
            JDALog.error(log, event, "Attempt to execute turned off command. Command: {}", command.getName());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class FollowedCommandArgumentNotExistException extends BotException {
        public FollowedCommandArgumentNotExistException(BotConfiguration config, CommandEventWrapper event, String cmd) {
            super(config, event.getGuild(), ExceptionLocaleSet.FOLLOWED_COMMAND_ARGUMENT_NOT_EXIST, Map.of(
                "passedCommand", cmd
            ), BugTracker.FOLLOWED_COMMAND_ARGUMENT_NOT_EXIST);
            JDALog.error(log, event, "Attempt to execute turned off on not existing command. Command: {}", cmd);
        }
    }
}
