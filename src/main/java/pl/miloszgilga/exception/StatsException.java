/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: StatsException.java
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
