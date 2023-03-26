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
 * The software is provided “as is”, without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.core;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import org.springframework.context.annotation.DependsOn;

import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotSlashCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.misc.QueueAfterParam;

import static pl.miloszgilga.exception.CommandException.MismatchCommandArgumentsCountException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@DependsOn("botConfiguration")
public abstract class AbstractCommand extends SlashCommand {

    private final int argsCount;
    private final BotCommand command;

    protected final BotConfiguration config;
    protected final EmbedMessageBuilder embedBuilder;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AbstractCommand(BotCommand command, BotConfiguration config, EmbedMessageBuilder embedBuilder) {
        this.name = command.getName();
        this.help = config.getLocaleText(command.getDescriptionHolder());
        this.ownerCommand = command.isOnlyOwner();
        this.argsCount = command.getArguments();
        this.aliases = command.getAliases();
        this.command = command;
        this.config = config;
        this.embedBuilder = embedBuilder;
        this.arguments = command.getArgSyntax();
        this.guildOnly = false;
        this.options = insertSlashOptionsData(command);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void execute(CommandEvent event) {
        final CommandEventWrapper commandEventWrapper = new CommandEventWrapper(event);
        try {
            final long args = Arrays.stream(event.getArgs().split("\\|")).filter(a -> a.length() > 0).count();
            if (args != argsCount) {
                throw new MismatchCommandArgumentsCountException(config, commandEventWrapper, command);
            }
            doExecuteCommand(commandEventWrapper);

            final var defferedMessages = event.getTextChannel().sendMessageEmbeds(commandEventWrapper.getEmbeds());
            final Consumer<QueueAfterParam> defferedSendConsumer = queueAfterParam -> {
                if (Objects.isNull(queueAfterParam)) {
                    defferedMessages.queue();
                } else {
                    defferedMessages.queueAfter(queueAfterParam.duration(), queueAfterParam.timeUnit());
                }
            };
            sendEmbeds(commandEventWrapper, defferedSendConsumer);
        } catch (BotException ex) {
            event.reply(embedBuilder.createErrorMessage(commandEventWrapper, ex));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void execute(SlashCommandEvent event) {
        final CommandEventWrapper commandEventWrapper = new CommandEventWrapper(event);
        event.deferReply().queue();
        try {
            doExecuteCommand(commandEventWrapper);

            final var defferedMessages = event.getHook().sendMessageEmbeds(commandEventWrapper.getEmbeds());
            final Consumer<QueueAfterParam> defferedSendConsumer = queueAfterParam -> {
                if (Objects.isNull(queueAfterParam)) {
                    defferedMessages.queue();
                } else {
                    defferedMessages.queueAfter(queueAfterParam.duration(), queueAfterParam.timeUnit());
                }
            };
            sendEmbeds(commandEventWrapper, defferedSendConsumer);
        } catch (BotException ex) {
            event.getHook()
                .sendMessageEmbeds(embedBuilder.createErrorMessage(commandEventWrapper, ex))
                .queue();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendEmbeds(CommandEventWrapper wrapper, Consumer<QueueAfterParam> defferedMessage) {
        final List<MessageEmbed> messageEmbeds = wrapper.getEmbeds();
        if (messageEmbeds.isEmpty()) return;

        final QueueAfterParam queueAfterParam = wrapper.getQueueAfterParam();
        defferedMessage.accept(queueAfterParam);

        if (!Objects.isNull(wrapper.getAppendAfterEmbeds())) {
            wrapper.getAppendAfterEmbeds().run();
        }
    }

    private List<OptionData> insertSlashOptionsData(BotCommand command) {
        final BotSlashCommand slashCommand = BotSlashCommand.getFromRegularCommand(command);
        return slashCommand.fabricateOptions(config);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteCommand(CommandEventWrapper event);
}
