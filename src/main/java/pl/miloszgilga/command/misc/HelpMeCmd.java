/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: HelpMeCmd.java
 * Last modified: 06/03/2023, 00:23
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

package pl.miloszgilga.command.misc;

import lombok.extern.slf4j.Slf4j;

import com.jagrosh.jdautilities.command.CommandEvent;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.JDACommand;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////s

@Slf4j
@JDAInjectableCommandLazyService
class HelpMeCmd extends JDACommand {

    HelpMeCmd(BotConfiguration jConfig, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.HELP_ME, jConfig, embedBuilder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Hello how a u? I m under the water. Please helpe me! Here too much raining n brlbrl...");
    }
}
