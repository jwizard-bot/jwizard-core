/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractPlaylistsCommand.java
 * Last modified: 29/04/2023, 01:23
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

package pl.miloszgilga.command;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractCommand;
import pl.miloszgilga.core.remote.RemoteModuleProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.ModuleException.PlaylistsModuleIsTurnedOffException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractPlaylistsCommand extends AbstractCommand {

    protected AbstractPlaylistsCommand(
        BotCommand command, BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler
    ) {
        super(command, config, embedBuilder, handler);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteCommand(CommandEventWrapper event) {
        if (!handler.getPossibleRemoteModuleProperty(RemoteModuleProperty.R_PLAYLISTS_MODULE_ENABLED, event.getGuild())) {
            throw new PlaylistsModuleIsTurnedOffException(config, event);
        }
        doExecutePlaylistsCommand(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecutePlaylistsCommand(CommandEventWrapper event);
}
