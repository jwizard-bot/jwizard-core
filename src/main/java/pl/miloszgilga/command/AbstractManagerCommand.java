/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractOwnerCommand.java
 * Last modified: 23/03/2023, 01:08
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

package pl.miloszgilga.command;

import net.dv8tion.jda.api.Permission;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractCommand;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.CommandException.UnauthorizedManagerCommandExecutionException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractManagerCommand extends AbstractCommand {

    public AbstractManagerCommand(BotCommand command, BotConfiguration config, EmbedMessageBuilder embedBuilder) {
        super(command, config, embedBuilder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteCommand(CommandEventWrapper event) {
        final boolean isNotOwner = !event.getAuthor().getId().equals(event.getGuild().getOwnerId());
        final boolean isNotManager = !event.getMember().hasPermission(Permission.MANAGE_SERVER);
        if (isNotOwner || isNotManager) {
            throw new UnauthorizedManagerCommandExecutionException(config, event);
        }
        doExecuteManagerCommand(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteManagerCommand(CommandEventWrapper event);
}
