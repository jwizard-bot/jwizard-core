/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ModuleException.java
 * Last modified: 28/04/2023, 22:34
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

public class ModuleException {

    @Slf4j public static class StatsModuleIsAlreadyRunningException extends BotException {
        public StatsModuleIsAlreadyRunningException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.STATS_MODULE_IS_ALREADY_RUNNING, Map.of(
                "statsTurnOffCmd", BotCommand.TURN_OFF_STATS_MODULE.parseWithPrefix(config)
            ), BugTracker.STATS_MODULE_IS_ALREADY_RUNNING);
            JDALog.error(log, event, "Attempt to invoke turn on stats command, while stats module is already running");
        }
    }

    @Slf4j public static class StatsModuleIsAlreadyDisabledException extends BotException {
        public StatsModuleIsAlreadyDisabledException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.STATS_MODULE_IS_ALREADY_DISABLED, Map.of(
                "statsTurnOnCmd", BotCommand.TURN_ON_STATS_MODULE.parseWithPrefix(config)
            ), BugTracker.STATS_MODULE_IS_ALREADY_DISABLED);
            JDALog.error(log, event, "Attempt to invoke turn off stats command, while stats module is already disabled");
        }
    }

    @Slf4j public static class StatsModuleIsTurnedOffException extends BotException {
        public StatsModuleIsTurnedOffException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.STATS_MODULE_IS_TURNED_OFF, BugTracker.STATS_MODULE_IS_TURNED_OFF);
            JDALog.error(log, event, "Attempt to invoke stats command, while stats module is disabled");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class MusicModuleIsAlreadyRunningException extends BotException {
        public MusicModuleIsAlreadyRunningException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.MUSIC_MODULE_IS_ALREADY_RUNNING, Map.of(
                "musicTurnOffCmd", BotCommand.TURN_OFF_MUSIC_MODULE.parseWithPrefix(config)
            ), BugTracker.MUSIC_MODULE_IS_ALREADY_RUNNING);
            JDALog.error(log, event, "Attempt to invoke turn on music command, while music module is already running");
        }
    }

    @Slf4j public static class MusicModuleIsAlreadyDisabledException extends BotException {
        public MusicModuleIsAlreadyDisabledException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.MUSIC_MODULE_IS_ALREADY_DISABLED, Map.of(
                "musicTurnOnCmd", BotCommand.TURN_ON_MUSIC_MODULE.parseWithPrefix(config)
            ), BugTracker.MUSIC_MODULE_IS_ALREADY_DISABLED);
            JDALog.error(log, event, "Attempt to invoke turn off music command, while music module is already disabled");
        }
    }

    @Slf4j public static class MusicModuleIsTurnedOffException extends BotException {
        public MusicModuleIsTurnedOffException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.MUSIC_MODULE_IS_TURNED_OFF, BugTracker.MUSIC_MODULE_IS_TURNED_OFF);
            JDALog.error(log, event, "Attempt to invoke music command, while music module is disabled");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class PlaylistsModuleIsAlreadyRunningException extends BotException {
        public PlaylistsModuleIsAlreadyRunningException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.PLAYLISTS_MODULE_IS_ALREADY_RUNNING, Map.of(
                "playlistsTurnOffCmd", BotCommand.TURN_OFF_PLAYLISTS_MODULE.parseWithPrefix(config)
            ), BugTracker.PLAYLISTS_MODULE_IS_ALREADY_RUNNING);
            JDALog.error(log, event, "Attempt to invoke turn on playlists command, while playlists module is already running");
        }
    }

    @Slf4j public static class PlaylistsModuleIsAlreadyDisabledException extends BotException {
        public PlaylistsModuleIsAlreadyDisabledException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.PLAYLISTS_MODULE_IS_ALREADY_DISABLED, Map.of(
                "playlistsTurnOnCmd", BotCommand.TURN_ON_PLAYLISTS_MODULE.parseWithPrefix(config)
            ), BugTracker.PLAYLISTS_MODULE_IS_ALREADY_DISABLED);
            JDALog.error(log, event, "Attempt to invoke turn off playlists command, while playlists module is already disabled");
        }
    }

    @Slf4j public static class PlaylistsModuleIsTurnedOffException extends BotException {
        public PlaylistsModuleIsTurnedOffException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.PLAYLISTS_MODULE_IS_TURNED_OFF, BugTracker.PLAYLISTS_MODULE_IS_TURNED_OFF);
            JDALog.error(log, event, "Attempt to invoke playlist command, while playlists module is disabled");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class VotingModuleIsAlreadyRunningException extends BotException {
        public VotingModuleIsAlreadyRunningException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.VOTING_MODULE_IS_ALREADY_RUNNING, Map.of(
                "votingTurnOffCmd", BotCommand.TURN_OFF_VOTING_MODULE.parseWithPrefix(config)
            ), BugTracker.VOTING_MODULE_IS_ALREADY_RUNNING);
            JDALog.error(log, event, "Attempt to invoke turn on voting command, while voting module is already running");
        }
    }

    @Slf4j public static class VotingModuleIsAlreadyDisabledException extends BotException {
        public VotingModuleIsAlreadyDisabledException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.VOTING_MODULE_IS_ALREADY_DISABLED, Map.of(
                "votingTurnOnCmd", BotCommand.TURN_ON_VOTING_MODULE.parseWithPrefix(config)
            ), BugTracker.VOTING_MODULE_IS_ALREADY_DISABLED);
            JDALog.error(log, event, "Attempt to invoke turn off voting command, while voting module is already disabled");
        }
    }

    @Slf4j public static class VotingModuleIsTurnedOffException extends BotException {
        public VotingModuleIsTurnedOffException(BotConfiguration config, CommandEventWrapper event) {
            super(config, ExceptionLocaleSet.VOTING_MODULE_IS_TURNED_OFF, BugTracker.VOTING_MODULE_IS_TURNED_OFF);
            JDALog.error(log, event, "Attempt to invoke voting command, while voting module is disabled");
        }
    }
}
