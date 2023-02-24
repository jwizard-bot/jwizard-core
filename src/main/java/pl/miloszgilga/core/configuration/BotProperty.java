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

import lombok.*;
import java.util.Arrays;

import static pl.miloszgilga.core.configuration.EnvProperty.*;
import static pl.miloszgilga.core.configuration.BotConfiguration.JPREFIX;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum BotProperty {
    J_DEVELOPMENT_MODE          ("development-mode"),
    J_PREFIX                    ("prefix"),
    J_AUTH_TOKEN                ("authorization.token",                                         true, TOKEN),
    J_APP_ID                    ("authorization.application-id",                                true, APP_ID),
    J_INACTIVITY_TIMEOUT        ("timeout.inactivity.max-time-minutes"),
    J_INACTIVITY_PAUSE_TIMEOUT  ("timeout.inactivity.max-time-after-pause-track-minutes"),
    J_INACTIVITY_VOTING_TIMEOUT ("timeout.voting.max-elapsed-time-minutes"),
    J_SHOW_FANCY_TITLE          ("misc.fancy-title.show"),
    J_FANCY_TITLE_PATH          ("misc.fancy-title.path-to-file"),
    J_SELECTED_LOCALE           ("misc.locale.selected-locale"),
    J_RR_ACTIVITY_ENABLED       ("misc.round-robin-activity.enable-sequencer"),
    J_RR_INTERVAL               ("misc.round-robin-activity.sequencer-inverval-seconds"),
    J_RR_RANDOMIZED             ("misc.round-robin-activity.randomized"),
    J_RR_EXTERNAL_FILE_ENABLED  ("misc.round-robin-activity.show-from-external-file.enabled"),
    J_RR_EXTERNAL_FILE_PATH     ("misc.round-robin-activity.show-from-external-file.path-to-file"),
    J_RR_COMMANDS_ENABLED       ("misc.round-robin-activity.show-commands.enabled"),
    J_PAGINATION_MAX            ("pagination.max-elements-per-page"),
    J_DB_CONNECTION             ("database.jdbc.connection",                                    true, DB_JDBC),
    J_DB_ENFORCE_SSL            ("database.jdbc.enforce-ssl"),
    J_DB_USERNAME               ("database.jdbc.username",                                      true, DB_USERNAME),
    J_DB_PASSWORD               ("database.jdbc.password",                                      true, DB_PASSWORD),
    J_DB_CREATE                 ("database.jdbc.create-if-not-exist"),
    J_HDB_SQL_OUT               ("database.hibernate.sql-on-output"),
    J_HDB_DRIVER                ("database.hibernate.driver-package"),
    J_HDB_DIALECT               ("database.hibernate.dialect-package"),
    J_HDB_HBM2DDL               ("database.hibernate.hbm2ddl-auto-mode");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private boolean isEnvVariable;
    private EnvProperty envProperty;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static BotProperty getBaseName(String name) {
        return Arrays.stream(BotProperty.values())
            .filter(v -> (JPREFIX + "." + v.name).equals(name))
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
