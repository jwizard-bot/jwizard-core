/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotException.java
 * Last modified: 16/05/2023, 18:58
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

import net.dv8tion.jda.api.entities.Guild;

import java.util.Map;
import java.util.HashMap;

import pl.miloszgilga.core.IEnumerableLocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class BotException extends RuntimeException {

    private final BugTracker bugTracker;
    private final Guild guild;
    private final IEnumerableLocaleSet langPattern;
    private final BotConfiguration config;
    private Map<String, Object> arguments = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BotException(BotConfiguration config, Guild guild, IEnumerableLocaleSet langPattern, BugTracker bugTracker) {
        this.config = config;
        this.guild = guild;
        this.langPattern = langPattern;
        this.bugTracker = bugTracker;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BotException(
        BotConfiguration config, Guild guild, IEnumerableLocaleSet langPattern, Map<String, Object> arguments,
        BugTracker bugTracker
    ) {
        this.config = config;
        this.guild = guild;
        this.langPattern = langPattern;
        this.arguments = arguments;
        this.bugTracker = bugTracker;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getMessage() {
        return config.getLocaleText(langPattern, guild, arguments);
    }

    public BugTracker getBugTracker() {
        return bugTracker;
    }
}
