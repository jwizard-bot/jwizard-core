/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotException.java
 * Last modified: 11/03/2023, 10:32
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
