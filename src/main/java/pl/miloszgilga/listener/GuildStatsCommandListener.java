/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildStatsCommandListener.java
 * Last modified: 19/03/2023, 14:06
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

package pl.miloszgilga.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.RemoteProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

import pl.miloszgilga.domain.guild_stats.IGuildStatsRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableListenerLazyService
public class GuildStatsCommandListener extends AbstractListenerAdapter {

    private final IGuildStatsRepository repository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    GuildStatsCommandListener(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IGuildStatsRepository repository
    ) {
        super(config, embedBuilder);
        this.repository = repository;
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
        return !config.getPossibleRemoteProperty(RemoteProperty.R_STATS_MODULE_ENABLED, guild, Boolean.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void onGuildMessageDelete(GuildMessageDeleteEvent event)                   { deleteMessageIncCounter(event); }
    @Override public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event)   { deleteReactionIncCounter(event); }
}
