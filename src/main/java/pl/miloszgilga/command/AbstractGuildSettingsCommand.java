/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractGuildSettingsCommand.java
 * Last modified: 15/05/2023, 15:57
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
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.cacheable.CacheableGuildSettingsDao;

import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractGuildSettingsCommand extends AbstractManagerCommand {

    protected final IGuildSettingsRepository repository;
    protected final CacheableGuildSettingsDao cacheableGuildSettingsDao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AbstractGuildSettingsCommand(
        BotCommand command, BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao
    ) {
        super(command, config, embedBuilder, handler);
        this.repository = repository;
        this.cacheableGuildSettingsDao = cacheableGuildSettingsDao;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteManagerCommand(CommandEventWrapper event) {
        doExecuteGuildSettingsCommand(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteGuildSettingsCommand(CommandEventWrapper event);
}
