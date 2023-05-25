/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ResetGuildStatsCmd.java
 * Last modified: 16/05/2023, 18:47
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

package pl.miloszgilga.command.manager;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractManagerStatsCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_stats.IGuildStatsRepository;
import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;

import static pl.miloszgilga.exception.StatsException.GuildHasNoStatsYetException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class ResetGuildStatsCmd extends AbstractManagerStatsCommand {

    private final IGuildStatsRepository guildStatsRepository;
    private final IMemberStatsRepository memberStatsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ResetGuildStatsCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository memberStatsRepository,
        IGuildStatsRepository guildStatsRepository, RemotePropertyHandler handler
    ) {
        super(BotCommand.RESET_GUILD_STATS, config, embedBuilder, handler);
        this.memberStatsRepository = memberStatsRepository;
        this.guildStatsRepository = guildStatsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteManagerStatsCommand(CommandEventWrapper event) {
        guildStatsRepository.findByGuild_DiscordId(event.getGuildId()).ifPresentOrElse(
            guildStats -> {
                guildStats.resetStats();
                guildStatsRepository.save(guildStats);
                memberStatsRepository.resetAllMembersStatsFromGuild(event.getGuildId());

                final MessageEmbed messageEmbed = embedBuilder
                    .createMessage(ResLocaleSet.GUILD_STATS_CLEARED_MESS, Map.of(
                        "guildName", event.getGuildName()
                    ), event.getGuild());
                event.sendEmbedMessage(messageEmbed);
                JDALog.info(log, event, "Stats of current guild was successfully cleared");
                },
            () -> { throw new GuildHasNoStatsYetException(config, event); }
        );
    }
}
