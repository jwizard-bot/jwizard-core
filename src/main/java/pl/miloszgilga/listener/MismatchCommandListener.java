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

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Set;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.misc.CommandWithArgsCount;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.JDAListenerAdapter;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

import static pl.miloszgilga.exception.CommandException.UnrecognizedCommandException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableListenerLazyService
class MismatchCommandListener extends JDAListenerAdapter {

    private final Set<CommandWithArgsCount> allCommandsWithAliases = BotCommand.getAllCommandsWithAliases();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MismatchCommandListener(BotConfiguration jConfig, EmbedMessageBuilder embedBuilder) {
        super(jConfig, embedBuilder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        try {
            String message = event.getMessage().getContentRaw();
            final int endPosition = message.indexOf(' ');
            if (endPosition > -1) {
                message = message.substring(0, message.indexOf(' '));
            }
            final String prefix = config.getProperty(BotProperty.J_PREFIX);

            if (event.getAuthor().isBot() || !message.startsWith(prefix)) return;
            final String rawMessage = message;
            if (allCommandsWithAliases.stream().noneMatch(c -> c.command().equals(rawMessage.substring(1)))) {
                throw new UnrecognizedCommandException(config, new EventWrapper(event));
            }
        } catch (BotException ex) {
            event.getChannel()
                .sendMessageEmbeds(embedBuilder.createErrorMessage(new EventWrapper(event), ex))
                .queue();
        }
    }
}
