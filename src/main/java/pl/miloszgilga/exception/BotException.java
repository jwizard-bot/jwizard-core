/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotException.java
 * Last modified: 05/03/2023, 00:00
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

package pl.miloszgilga.exception;

import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

import java.util.Map;
import java.util.HashMap;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class BotException extends RuntimeException {

    private final BugTracker bugTracker;
    private final LocaleSet langPattern;
    private final BotConfiguration config;
    private Map<String, Object> arguments = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BotException(BotConfiguration config, LocaleSet langPattern, BugTracker bugTracker) {
        this.config = config;
        this.langPattern = langPattern;
        this.bugTracker = bugTracker;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BotException(BotConfiguration config, LocaleSet langPattern, Map<String, Object> arguments, BugTracker bugTracker) {
        this.config = config;
        this.langPattern = langPattern;
        this.arguments = arguments;
        this.bugTracker = bugTracker;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getMessage() {
        return config.getLocaleText(LocaleSet.findByHolder(langPattern.getHolder()), arguments);
    }

    public BugTracker getBugTracker() {
        return bugTracker;
    }
}
