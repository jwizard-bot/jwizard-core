/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractManagerCommand.java
 * Last modified: 23/03/2023, 01:18
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
