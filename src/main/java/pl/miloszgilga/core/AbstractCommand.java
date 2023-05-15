/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractCommand.java
 * Last modified: 19/03/2023, 21:41
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

package pl.miloszgilga.core;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import org.springframework.context.annotation.DependsOn;

import java.util.Map;
import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.QueueAfterParam;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@DependsOn("botConfiguration")
public abstract class AbstractCommand extends SlashCommand {

    private final BotCommand command;

    protected final BotConfiguration config;
    protected final RemotePropertyHandler handler;
    protected final EmbedMessageBuilder embedBuilder;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AbstractCommand(
        BotCommand command, BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler
    ) {
        this.name = command.getName();
        this.help = config.getLocaleText(command.getDescriptionLocaleSet());
        this.aliases = command.getAliases();
        this.command = command;
        this.config = config;
        this.handler = handler;
        this.embedBuilder = embedBuilder;
        this.arguments = config.getLocaleText(command.getArgSyntax());
        this.guildOnly = false;
        this.options = BotCommandArgument.fabricateSlashOptions(config, command);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void execute(CommandEvent event) {
        final CommandEventWrapper commandEventWrapper = new CommandEventWrapper(event);
        try {
            final Map<BotCommandArgument, String> arguments = BotCommandArgument
                .extractForBaseCommand(event.getArgs(), command, config, commandEventWrapper);
            commandEventWrapper.setArgs(arguments);

            doExecuteCommand(commandEventWrapper);
            sendEmbedsFromCommand(event, commandEventWrapper);
        } catch (BotException ex) {
            event.reply(embedBuilder.createErrorMessage(commandEventWrapper, ex));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void execute(SlashCommandEvent event) {
        final CommandEventWrapper commandEventWrapper = new CommandEventWrapper(event);
        commandEventWrapper.setArgs(BotCommandArgument
            .extractForSlashCommand(config, commandEventWrapper, event.getOptions(), command));

        event.deferReply().queue();
        try {
            doExecuteCommand(commandEventWrapper);
            sendEmbedsFromSlashCommand(event, commandEventWrapper);
        } catch (BotException ex) {
            event.getHook()
                .sendMessageEmbeds(embedBuilder.createErrorMessage(commandEventWrapper, ex))
                .queue();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendEmbedsFromCommand(CommandEvent event, CommandEventWrapper wrapper) {
        if (wrapper.getEmbeds().isEmpty()) return;
        final RestAction<Message> defferedMessages = event.getTextChannel().sendMessageEmbeds(wrapper.getEmbeds());
        sendEmbedsFromAnyCommand(defferedMessages, wrapper);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendEmbedsFromSlashCommand(SlashCommandEvent event, CommandEventWrapper wrapper) {
        if (wrapper.getEmbeds().isEmpty()) return;

        if (event.getHook().isExpired()) {
            final var defferedMessages = event.getTextChannel().sendMessageEmbeds(wrapper.getEmbeds());
            sendEmbedsFromAnyCommand(defferedMessages, wrapper);
            return;
        }
        final RestAction<Message> defferedMessages = event.getHook().sendMessageEmbeds(wrapper.getEmbeds());
        sendEmbedsFromAnyCommand(defferedMessages, wrapper);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendEmbedsFromAnyCommand(RestAction<Message> defferedMessages, CommandEventWrapper wrapper) {
        final QueueAfterParam afterParam = wrapper.getQueueAfterParam();

        if (Objects.isNull(afterParam)) {
            if (Objects.isNull(wrapper.getAppendAfterEmbeds())) {
                defferedMessages.queue();
                return;
            }
            defferedMessages.queue(v -> wrapper.getAppendAfterEmbeds().run());
            return;
        }
        if (Objects.isNull(wrapper.getAppendAfterEmbeds())) {
            defferedMessages.queueAfter(afterParam.duration(), afterParam.timeUnit());
            return;
        }
        final var scheduledFuture = defferedMessages.queueAfter(afterParam.duration(), afterParam.timeUnit());
        if (!scheduledFuture.isDone()) return;
        wrapper.getAppendAfterEmbeds().run();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteCommand(CommandEventWrapper event);
}
