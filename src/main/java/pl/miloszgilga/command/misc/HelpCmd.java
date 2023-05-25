/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: HelpCmd.java
 * Last modified: 16/05/2023, 19:23
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
