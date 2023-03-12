/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JDACommand.java
 * Last modified: 23/02/2023, 19:09
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

package pl.miloszgilga.core;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import org.springframework.context.annotation.DependsOn;

import java.util.Arrays;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.CommandException.MismatchCommandArgumentsCountException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@DependsOn("botConfiguration")
public abstract class AbstractCommand extends Command {

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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void execute(CommandEvent event) {
        try {
            final long args = Arrays.stream(event.getArgs().split("\\|")).filter(a -> a.length() > 0).count();
            if (args != argsCount) {
                throw new MismatchCommandArgumentsCountException(config, new EventWrapper(event), command);
            }
            doExecuteCommand(event);
        } catch (BotException ex) {
            event.getChannel()
                .sendMessageEmbeds(embedBuilder.createErrorMessage(new EventWrapper(event), ex))
                .queue();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteCommand(CommandEvent event);
}
