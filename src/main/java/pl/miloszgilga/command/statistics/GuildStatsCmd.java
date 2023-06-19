/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildStatsCmd.java
 * Last modified: 29/04/2023, 01:48
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

package pl.miloszgilga.command.statistics;

import net.dv8tion.jda.api.entities.MessageEmbed;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractStatsCommand;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_stats.IGuildStatsRepository;
import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;

import pl.miloszgilga.exception.StatsException.GuildHasNoStatsYetException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class GuildStatsCmd extends AbstractStatsCommand {

    private final IGuildStatsRepository statsRepository;
    private final IMemberStatsRepository memberStatsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    GuildStatsCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IGuildStatsRepository statsRepository,
        IMemberStatsRepository memberStatsRepository, RemotePropertyHandler handler,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.GUILD_STATS, config, embedBuilder, handler, cacheableCommandStateDao);
        this.statsRepository = statsRepository;
        this.memberStatsRepository = memberStatsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteStatsCommand(CommandEventWrapper event) {
        statsRepository.findByGuild_DiscordId(event.getGuildId()).ifPresentOrElse(
            guildStats -> memberStatsRepository.getAllMemberStats(event.getGuildId()).ifPresentOrElse(
                statsDto -> {
                    final MessageEmbed messageEmbed = embedBuilder.createGuildStatsMessage(event, guildStats, statsDto);
                    event.sendEmbedMessage(messageEmbed);
                },
                () -> { throw new GuildHasNoStatsYetException(config, event); }
            ),
            () -> { throw new GuildHasNoStatsYetException(config, event); }
        );
    }
}
