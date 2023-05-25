/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SettingsException.java
 * Last modified: 17/05/2023, 14:57
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

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.locale.ExceptionLocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class SettingsException {

    @Slf4j public static class ChannelIsNotTextChannelException extends BotException {
        public ChannelIsNotTextChannelException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.CHANNEL_IS_NOT_TEXT_CHANNEL,
                BugTracker.CHANNEL_IS_NOT_TEXT_CHANNEL);
            JDALog.error(log, event, "Attempt to invoke command, while passed channel is not text channel");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class RoleAlreadyExistException extends BotException {
        public RoleAlreadyExistException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.ROLE_ALREADY_EXIST, BugTracker.ROLE_ALREADY_EXIST);
            JDALog.error(log, event, "Attempt to create/change/replace role with already existing name");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class LocaleNotFoundException extends BotException {
        public LocaleNotFoundException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.LOCALE_NOT_EXIST, Map.of(
                "availableLocales", config.getProperty(BotProperty.J_AVAILABLE_LOCALES).replaceAll(",", ", ")
            ), BugTracker.LOCALE_NOT_EXIST);
            JDALog.error(log, event, "Attempt to assign not existing locale");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class MaxRepeatsOutOfBoundsException extends BotException {
        public MaxRepeatsOutOfBoundsException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.MAX_REPEATS_OUT_OF_BOUNDS, Map.of(
                "maxRepeatsCount", config.getProperty(BotProperty.J_MAX_REPEATS_SINGLE_TRACK)
            ), BugTracker.MAX_REPEATS_OUT_OF_BOUNDS);
            JDALog.error(log, event, "Attempt to set max repeats greater than default max repeats value");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class PercentageOutOfBoundsException extends BotException {
        public PercentageOutOfBoundsException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.PERCENTAGE_OUT_OF_BOUNDS, BugTracker.PERCENTAGE_OUT_OF_BOUNDS);
            JDALog.error(log, event, "Attempt to assign percentage value below 0 or greater than 100");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TimeToEndVotingOutOfBoundsException extends BotException {
        public TimeToEndVotingOutOfBoundsException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.TIME_TO_END_VOTING_OUT_OF_BOUNDS, Map.of(
                "maxSeconds", config.getProperty(BotProperty.J_MAX_INACTIVITY_VOTING_TIME)
            ), BugTracker.TIME_TO_END_VOTING_OUT_OF_BOUNDS);
            JDALog.error(log, event, "Attempt to assign time to end voting seconds value below 5 and greater than default");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TimeToLeaveEmptyChannelOutOfBoundsException extends BotException {
        public TimeToLeaveEmptyChannelOutOfBoundsException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.TIME_TO_LEAVE_EMPTY_CHANNEL_OUT_OF_BOUNDS, Map.of(
                "maxSeconds", config.getProperty(BotProperty.J_MAX_INACTIVITY_EMPTY_TIME)
            ), BugTracker.TIME_TO_LEAVE_EMPTY_OUT_OF_BOUNDS);
            JDALog.error(log, event, "Attempt to assign time to leave empty seconds value below 5 or greater than default");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TimeToLeaveNoTracksOutOfBoundsException extends BotException {
        public TimeToLeaveNoTracksOutOfBoundsException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.TIME_TO_LEAVE_NO_TRACKS_OUT_OF_BOUNDS, Map.of(
                "maxSeconds", config.getProperty(BotProperty.J_MAX_INACTIVITY_NO_TRACK_TIME)
            ), BugTracker.TIME_TO_LEAVE_NO_TRACKS_OUT_OF_BOUNDS);
            JDALog.error(log, event, "Attempt to assign time to leave no tracks seconds value below 5 or greater than default");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class InsufficientPermissionRoleHierarchyException extends BotException {
        public InsufficientPermissionRoleHierarchyException(
            BotConfiguration config, CommandEventWrapper event, String botRoleName
        ) {
            super(config, event.getGuild(), ExceptionLocaleSet.INSUFFICIENT_PERMISSION_ROLE_HIERARCHY, Map.of(
                "botRole", botRoleName
            ), BugTracker.INSUFFICIENT_PERMISSION_ROLE_HIERARCHY);
            JDALog.error(log, event, "Attempt to change role name with insufficient permissions (hierarchy)");
        }
    }
}
