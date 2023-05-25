/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractMyStatsCommand.java
 * Last modified: 29/04/2023, 01:21
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

import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;

import static pl.miloszgilga.exception.StatsException.YouHasNoStatsYetInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractMyStatsCommand extends AbstractStatsCommand {

    private final IMemberStatsRepository repository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected AbstractMyStatsCommand(
        BotCommand command, BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository repository,
        RemotePropertyHandler handler
    ) {
        super(command, config, embedBuilder, handler);
        this.repository = repository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteStatsCommand(CommandEventWrapper event) {
        final boolean statsExist = repository.existsByMember_DiscordIdAndGuild_DiscordId(event.getMember().getId(),
            event.getGuild().getId());
        if (!statsExist) {
            throw new YouHasNoStatsYetInGuildException(config, event);
        }
        doExecuteMyStatsCommand(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteMyStatsCommand(CommandEventWrapper event);
}
