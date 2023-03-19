/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: HelpCmd.java
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

import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.HelpEmbedContent;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.embed.EmbedPaginationBuilder;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.AbstractCommand;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
class HelpCmd extends AbstractCommand {

    private final EmbedPaginationBuilder paginate;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    HelpCmd(BotConfiguration config, EmbedMessageBuilder embedBuilder, EmbedPaginationBuilder paginate) {
        super(BotCommand.HELP, config, embedBuilder);
        this.paginate = paginate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteCommand(CommandEventWrapper event) {
        final HelpEmbedContent content = new HelpEmbedContent(
            config.getLocaleText(LocaleSet.HELP_INFO_SOURCE_CODE_LINK_MESS,
                Map.of("sourceCodeLink", config.getProperty(BotProperty.J_SOURCE_CODE_PATH))),
            String.format("jre%s_%s", Runtime.version().feature(), config.getProjectVersion()),
            BotCommand.count()
        );
        final MessageEmbed messageEmbed = embedBuilder.createHelpMessage(event, content);
        final Paginator paginator = paginate.createDefaultPaginator(BotCommand.getCommandsAsEmbedContent(config));

        event.appendEmbedMessage(messageEmbed, () -> paginator.display(event.getTextChannel()));
    }
}
