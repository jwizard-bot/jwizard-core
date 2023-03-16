/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JProperty.java
 * Last modified: 22/02/2023, 22:48
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
    J_DEVELOPMENT_MODE              ("development-mode"),
    J_PREFIX                        ("prefix"),
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
    J_INACTIVITY_VOTING_TIMEOUT     ("timeout.voting.max-elapsed-time-after-finish"),
    J_MAX_REPEATS_SINGLE_TRACK      ("audio.max-repeats-single-track"),
    J_DEFAULT_PLAYER_VOLUME_UNITS   ("audio.default-player-volume-units"),
    J_DJ_ROLE_NAME                  ("misc.dj-role-name"),
    J_SHOW_FANCY_TITLE              ("misc.fancy-title.show"),
    J_FANCY_TITLE_PATH              ("misc.fancy-title.path-to-file"),
    J_SELECTED_LOCALE               ("misc.locale.selected-locale"),
    J_RR_ACTIVITY_ENABLED           ("misc.round-robin-activity.enable-sequencer"),
    J_RR_INTERVAL                   ("misc.round-robin-activity.sequencer-inverval-seconds"),
    J_RR_RANDOMIZED                 ("misc.round-robin-activity.randomized"),
    J_RR_EXTERNAL_FILE_ENABLED      ("misc.round-robin-activity.show-from-external-file.enabled"),
    J_RR_EXTERNAL_FILE_PATH         ("misc.round-robin-activity.show-from-external-file.path-to-file"),
    J_RR_COMMANDS_ENABLED           ("misc.round-robin-activity.show-commands.enabled"),
    J_PAGINATION_MAX                ("pagination.max-elements-per-page"),
    J_DB_CONNECTION                 ("database.jdbc.connection",                                    true, EnvProperty.DB_JDBC),
    J_DB_ENFORCE_SSL                ("database.jdbc.enforce-ssl"),
    J_DB_USERNAME                   ("database.jdbc.username",                                      true, EnvProperty.DB_USERNAME),
    J_DB_PASSWORD                   ("database.jdbc.password",                                      true, EnvProperty.DB_PASSWORD),
    J_DB_CREATE                     ("database.jdbc.create-if-not-exist"),
    J_HDB_SQL_OUT                   ("database.hibernate.sql-on-output"),
    J_HDB_DRIVER                    ("database.hibernate.driver-package"),
    J_HDB_DIALECT                   ("database.hibernate.dialect-package"),
    J_HDB_HBM2DDL                   ("database.hibernate.hbm2ddl-auto-mode");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private boolean isEnvVariable;
    private EnvProperty envProperty;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static BotProperty getBaseName(String name) {
        return Arrays.stream(BotProperty.values())
            .filter(v -> (BotConfiguration.JPREFIX + "." + v.name).equals(name))
            .findFirst()
            .orElseThrow(() -> {
                throw new IllegalArgumentException("Property " + name + " not exist.");
            });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static EnvPropertyHolder getEnvProperty(String name, AppMode appMode) {
        final BotProperty botProperty = getBaseName(name);
        if (!botProperty.isEnvVariable) return null;
        final String placeholder = "${env:" + appMode.getMode() + "_" + botProperty.getEnvProperty().getName() + "}";
        return new EnvPropertyHolder(placeholder, appMode.getMode() + "_" + botProperty.envProperty.getName());
    }
}
