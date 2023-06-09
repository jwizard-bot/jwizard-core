/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractCommand.java
 * Last modified: 17/05/2023, 01:42
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
        this.aliases = command.getAliases().toArray(String[]::new);
        this.command = command;
        this.config = config;
        this.handler = handler;
        this.embedBuilder = embedBuilder;
        this.arguments = command.prepareArgs(config);
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
