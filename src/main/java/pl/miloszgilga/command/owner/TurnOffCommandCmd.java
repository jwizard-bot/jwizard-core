/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: TurnOffCommandCmd.java
 * Last modified: 6/15/23, 5:57 PM
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

package pl.miloszgilga.command.owner;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.CommandWithProxyDto;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.command.AbstractOwnerCommand;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class TurnOffCommandCmd extends AbstractOwnerCommand {

    private final CacheableCommandStateDao cacheableCommandStateDao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    TurnOffCommandCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.TURN_OFF_COMMAND, config, embedBuilder, handler, cacheableCommandStateDao);
        this.cacheableCommandStateDao = cacheableCommandStateDao;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteOwnerCommand(CommandEventWrapper event) {
        final String commandNameOrAlias = event.getArgumentAndParse(BotCommandArgument.TURN_OFF_COMMAND_COMMAND_TAG);

        final CommandWithProxyDto payload = BotCommand.getCategoryFromRawCommand(commandNameOrAlias, config, event);
        cacheableCommandStateDao.findCategoryWithCommandAndSave(payload, event, false);

        final String cmdName = payload.command().getName();
        final MessageEmbed messageEmbed = embedBuilder.createMessage(ResLocaleSet.SUCCESS_TURN_OFF_COMMAND_MESS, Map.of(
            "command", cmdName,
            "turnOnCmd", BotCommand.TURN_ON_COMMAND.parseWithPrefix(config, cmdName)
        ), event.getGuild());

        event.sendEmbedMessage(messageEmbed);
    }
}
