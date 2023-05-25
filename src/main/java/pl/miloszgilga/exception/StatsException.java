/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: StatsException.java
 * Last modified: 16/05/2023, 19:01
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

import net.dv8tion.jda.api.entities.User;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.locale.ExceptionLocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class StatsException {

    @Slf4j public static class MemberHasNoStatsYetInGuildException extends BotException {
        public MemberHasNoStatsYetInGuildException(BotConfiguration config, CommandEventWrapper event, User user) {
            super(config, event.getGuild(), ExceptionLocaleSet.MEMBER_HAS_NO_STATS_YET_IN_GUILD, Map.of(
                "memberTag", user.getAsTag()
            ), BugTracker.MEMBER_HAS_NO_STATS_YET);
            JDALog.error(log, event, "Attempt to invoke non existing stats from member: %s", user.getAsTag());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class YouHasNoStatsYetInGuildException extends BotException {
        public YouHasNoStatsYetInGuildException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.YOU_HAS_NO_STATS_YET_IN_GUILD, BugTracker.YOU_HAS_NO_STATS_YET);
            JDALog.error(log, event, "Attempt to invoke non existing stats by sender: %s",
                event.getMember().getUser().getAsTag());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class GuildHasNoStatsYetException extends BotException {
        public GuildHasNoStatsYetException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.GUILD_HAS_NO_STATS_YET, Map.of(
                "guildName", event.getGuild().getName()
            ), BugTracker.GUILD_HAS_NO_STATS_YET);
            JDALog.error(log, event, "Attempt to invoke non existing stats in guild: %s", event.getGuild().getName());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class StatsAlreadyPublicException extends BotException {
        public StatsAlreadyPublicException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.STATS_ALREADY_PUBLIC, Map.of(
                "statsPrivateCmd", BotCommand.PRIVATE_STATS.parseWithPrefix(config)
            ), BugTracker.STATS_ALREADY_PUBLIC);
            JDALog.error(log, event, "Attempt to change already public stats to public");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class StatsAlreadyPrivateException extends BotException {
        public StatsAlreadyPrivateException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.STATS_ALREADY_PRIVATE, Map.of(
                "statsPublicCmd", BotCommand.PUBLIC_STATS.parseWithPrefix(config)
            ), BugTracker.STATS_ALREADY_PRIVATE);
            JDALog.error(log, event, "Attempt to change already private stats to private");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class StatsAlreadyEnabledException extends BotException {
        public StatsAlreadyEnabledException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.STATS_ALREADY_ENABLED, Map.of(
                "disableStatsCmd", BotCommand.DISABLE_STATS.parseWithPrefix(config)
            ), BugTracker.STATS_ALREADY_ENABLED);
            JDALog.error(log, event, "Attempt to change already enabled stats to enabled");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class StatsAlreadyDisabledException extends BotException {
        public StatsAlreadyDisabledException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.STATS_ALREADY_DISABLED, Map.of(
                "enableStatsCmd", BotCommand.ENABLE_STATS.parseWithPrefix(config)
            ), BugTracker.STATS_ALREADY_DISABLED);
            JDALog.error(log, event, "Attempt to change already disabled stats to disabled");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class YouHasDisableStatsException extends BotException {
        public YouHasDisableStatsException(BotConfiguration config, CommandEventWrapper event) {
            super(config, event.getGuild(), ExceptionLocaleSet.YOU_HAS_DISABLED_STATS, Map.of(
                "enableStatsCmd", BotCommand.ENABLE_STATS.parseWithPrefix(config)
            ), BugTracker.MEMBER_HAS_STATS_DISABLED);
            JDALog.error(log, event, "Attempt to get your stats, where is disabled");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class MemberHasDisableStatsException extends BotException {
        public MemberHasDisableStatsException(BotConfiguration config, CommandEventWrapper event, User user) {
            super(config, event.getGuild(), ExceptionLocaleSet.MEMBER_HAS_DISABLED_STATS, Map.of(
                "memberTag", user.getAsTag()
            ), BugTracker.MEMBER_HAS_STATS_DISABLED);
            JDALog.error(log, event, "Attempt to get stats from member '%s' who has disabled stats", user.getAsTag());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class MemberHasPrivateStatsException extends BotException {
        public MemberHasPrivateStatsException(BotConfiguration config, CommandEventWrapper event, User user) {
            super(config, event.getGuild(), ExceptionLocaleSet.MEMBER_HAS_PRIVATE_STATS, Map.of(
                "memberTag", user.getAsTag()
            ), BugTracker.MEMBER_HAS_STATS_PRIVATE);
            JDALog.error(log, event, "Attempt to get stats from member '%s' who has private stats", user.getAsTag());
        }
    }
}
