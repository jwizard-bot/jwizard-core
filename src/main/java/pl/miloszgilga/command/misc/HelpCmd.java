/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: HelpCmd.java
 * Last modified: 19/03/2023, 14:54
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

package pl.miloszgilga.command.misc;

import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.HelpEmbedContent;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.embed.EmbedPaginationBuilder;
import pl.miloszgilga.core.AbstractCommand;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
class HelpCmd extends AbstractCommand {

    private final EmbedPaginationBuilder paginate;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    HelpCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, EmbedPaginationBuilder paginate,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.HELP, config, embedBuilder, handler);
        this.paginate = paginate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteCommand(CommandEventWrapper event) {
        final HelpEmbedContent content = new HelpEmbedContent(
            config.getLocaleText(ResLocaleSet.HELP_INFO_SOURCE_CODE_LINK_MESS, event.getGuild(),
                Map.of("sourceCodeLink", config.getProperty(BotProperty.J_SOURCE_CODE_PATH))),
            String.format("jre%s_%s", Runtime.version().feature(), config.getProjectVersion()),
            BotCommand.count()
        );
        final MessageEmbed messageEmbed = embedBuilder.createHelpMessage(event, content);
        final Paginator paginator = paginate
            .createDefaultPaginator(BotCommand.getCommandsAsEmbedContent(config, event.getGuild()));

        event.appendEmbedMessage(messageEmbed, () -> paginator.display(event.getTextChannel()));
    }
}
