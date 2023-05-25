/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildStatsCommandListener.java
 * Last modified: 29/04/2023, 01:28
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

package pl.miloszgilga.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.remote.RemoteModuleProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

import pl.miloszgilga.domain.guild_stats.IGuildStatsRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableListenerLazyService
public class GuildStatsCommandListener extends AbstractListenerAdapter {

    private final RemotePropertyHandler handler;
    private final IGuildStatsRepository repository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    GuildStatsCommandListener(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IGuildStatsRepository repository,
        RemotePropertyHandler handler
    ) {
        super(config, embedBuilder);
        this.repository = repository;
        this.handler = handler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void deleteMessageIncCounter(GenericGuildEvent event) {
        if (collectorIsDisabled(event.getGuild())) return;
        if (!repository.existsByGuild_DiscordId(event.getGuild().getId())) return;

        repository.increaseDeletedMessages(event.getGuild().getId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void deleteReactionIncCounter(GenericGuildEvent event) {
        if (collectorIsDisabled(event.getGuild())) return;
        if (!repository.existsByGuild_DiscordId(event.getGuild().getId())) return;

        repository.increaseDeletedReactions(event.getGuild().getId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean collectorIsDisabled(Guild guild) {
        return !handler.getPossibleRemoteModuleProperty(RemoteModuleProperty.R_STATS_MODULE_ENABLED, guild);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void onGuildMessageDelete(GuildMessageDeleteEvent event)                   { deleteMessageIncCounter(event); }
    @Override public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event)   { deleteReactionIncCounter(event); }
}
