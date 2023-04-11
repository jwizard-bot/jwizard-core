/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotProperty.java
 * Last modified: 19/03/2023, 20:57
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

package pl.miloszgilga.core.configuration;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum BotProperty {

    J_NAME                          ("name"),
    J_SOURCE_CODE_PATH              ("source-code-path"),
    J_PREFIX                        ("prefix"),
    J_SLASH_COMMANDS_ENABLED        ("slash-commands.enabled"),
    J_HAS_AVATAR                    ("avatar.has-avatar"),
    J_PATH_TO_AVATAR                ("avatar.path-to-avatar"),
    J_AVATAR_DAY_NIGHT_ENABLED      ("avatar.day-night-mode.enabled"),
    J_AVATAR_DAY_NIGHT_TIMEZONE     ("avatar.day-night-mode.timezone"),
    J_AVATAR_DAY_TRIGGER            ("avatar.day-night-mode.day.trigger-hour"),
    J_PATH_TO_AVATAR_DAY_MODE       ("avatar.day-night-mode.day.path-to-avatar"),
    J_AVATAR_NIGHT_TRIGGER          ("avatar.day-night-mode.night.trigger-hour"),
    J_PATH_TO_AVATAR_NIGHT_MODE     ("avatar.day-night-mode.night.path-to-avatar"),
    J_AUTH_TOKEN                    ("authorization.token",                                         true, EnvProperty.TOKEN),
    J_APP_ID                        ("authorization.application-id",                                true, EnvProperty.APP_ID),
    J_INACTIVITY_EMPTY_TIMEOUT      ("timeout.inactivity.max-time-after-leave-empty-channel"),
    J_INACTIVITY_NO_TRACK_TIMEOUT   ("timeout.inactivity.max-time-after-leave-no-track-channel"),
    J_INACTIVITY_VOTING_TIMEOUT     ("voting.max-elapsed-time-after-finish"),
    J_VOTING_PERCENTAGE_RATIO       ("voting.percentage-ratio"),
    J_MAX_REPEATS_SINGLE_TRACK      ("audio.max-repeats-single-track"),
    J_DEFAULT_PLAYER_VOLUME_UNITS   ("audio.default-player-volume-units"),
    J_DJ_ROLE_NAME                  ("misc.dj-role-name"),
    J_SELECTED_LOCALE               ("misc.locale.selected-locale"),
    J_RR_ACTIVITY_ENABLED           ("misc.round-robin-activity.enable-sequencer"),
    J_RR_INTERVAL                   ("misc.round-robin-activity.sequencer-inverval-seconds"),
    J_RR_RANDOMIZED                 ("misc.round-robin-activity.randomized"),
    J_RR_EXTERNAL_FILE_ENABLED      ("misc.round-robin-activity.show-from-external-file.enabled"),
    J_RR_EXTERNAL_FILE_PATH         ("misc.round-robin-activity.show-from-external-file.path-to-file"),
    J_RR_COMMANDS_ENABLED           ("misc.round-robin-activity.show-commands.enabled"),
    J_PAGINATION_MAX                ("pagination.max-elements-per-page"),
    J_PAGINATION_MENU_IS_ALIVE      ("pagination.menu-is-alive-seconds"),
    J_STATS_MODULE_ENABLED          ("modules.stats-module-enabled");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private boolean isEnvVariable;
    private EnvProperty envProperty;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static BotProperty getBaseName(String name) {
        return Arrays.stream(BotProperty.values())
            .filter(v -> (BotConfiguration.JPREFIX + "." + v.name).equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Property " + name + " not exist."));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static EnvPropertyHolder getEnvProperty(String name, AppMode appMode) {
        final BotProperty botProperty = getBaseName(name);
        if (!botProperty.isEnvVariable) return null;
        final String placeholder = "${env:" + appMode.getMode() + "_" + botProperty.getEnvProperty().getName() + "}";
        return new EnvPropertyHolder(placeholder, appMode.getMode() + "_" + botProperty.envProperty.getName());
    }
}
