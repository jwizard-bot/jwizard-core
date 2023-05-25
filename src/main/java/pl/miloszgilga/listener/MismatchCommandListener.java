/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MismatchCommandListener.java
 * Last modified: 29/04/2023, 01:30
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

package pl.miloszgilga.listener;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Set;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.misc.CommandWithArgsCount;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

import static pl.miloszgilga.exception.CommandException.UnrecognizedCommandException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableListenerLazyService
class MismatchCommandListener extends AbstractListenerAdapter {

    private final Set<CommandWithArgsCount> allCommandsWithAliases = BotCommand.getAllCommandsWithAliases();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MismatchCommandListener(BotConfiguration jConfig, EmbedMessageBuilder embedBuilder) {
        super(jConfig, embedBuilder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final CommandEventWrapper commandEventWrapper = new CommandEventWrapper(event);
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
                throw new UnrecognizedCommandException(config, commandEventWrapper);
            }
        } catch (BotException ex) {
            event.getChannel()
                .sendMessageEmbeds(embedBuilder.createErrorMessage(commandEventWrapper, ex))
                .queue();
        }
    }
}
