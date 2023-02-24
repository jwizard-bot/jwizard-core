/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MismatchCommandListener.java
 * Last modified: 23/02/2023, 22:18
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

package pl.miloszgilga.listener;

import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import pl.miloszgilga.core.JDAListenerAdapter;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAListenerLazyService;

import static pl.miloszgilga.core.configuration.BotProperty.J_PREFIX;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAListenerLazyService
class MismatchCommandListener extends JDAListenerAdapter {

    MismatchCommandListener(BotConfiguration jConfig) {
        super(jConfig);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        final boolean startsWithPrefix = event.getMessage().getContentRaw().startsWith(config.getProperty(J_PREFIX));
        if (event.getAuthor().isBot() || !startsWithPrefix) return;

        log.info("MismatchCommandListener invoke...");
        event.getChannel().sendMessage("MismatchCommandListener").queue();
    }
}
