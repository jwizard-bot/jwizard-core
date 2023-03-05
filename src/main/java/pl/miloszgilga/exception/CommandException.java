/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandException.java
 * Last modified: 05/03/2023, 23:09
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

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class CommandException {

    @Slf4j public static class UnrecognizedCommandException extends BotException {
        public UnrecognizedCommandException(BotConfiguration config, EventWrapper event) {
            super(config, LocaleSet.UNRECOGNIZED_COMMAND_EXC.getHolder(), Map.of(
                "helpCmd", config.getProperty(BotProperty.J_PREFIX) + BotCommand.HELP.getName(),
                "helpmeCmd", config.getProperty(BotProperty.J_PREFIX) + BotCommand.HELP_ME.getName()
            ));
            log.debug("G: {}, A: {} <> Unrecognized command exception. Command not found: {}",
                event.guildName(), event.authorTag(), event.message());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
