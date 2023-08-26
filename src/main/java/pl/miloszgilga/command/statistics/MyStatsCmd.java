/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MyStatsCmd.java
 * Last modified: 29/04/2023, 01:49
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
import pl.miloszgilga.command.AbstractMyStatsCommand;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;
import pl.miloszgilga.domain.member_settings.MemberSettingsEntity;
import pl.miloszgilga.domain.member_settings.IMemberSettingsRepository;

import static pl.miloszgilga.exception.StatsException.YouHasDisableStatsException;
import static pl.miloszgilga.exception.StatsException.YouHasNoStatsYetInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class MyStatsCmd extends AbstractMyStatsCommand {

    private final IMemberStatsRepository statsRepository;
    private final IMemberSettingsRepository settingsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MyStatsCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository repository,
        IMemberStatsRepository statsRepository, IMemberSettingsRepository settingsRepository, RemotePropertyHandler handler,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.MY_STATS, config, embedBuilder, repository, handler, cacheableCommandStateDao);
        this.statsRepository = statsRepository;
        this.settingsRepository = settingsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMyStatsCommand(CommandEventWrapper event) {
        final MemberSettingsEntity settings = settingsRepository
            .findByMember_DiscordIdAndGuild_DiscordId(event.getMemberId(), event.getGuildId())
            .orElseThrow(() -> new YouHasNoStatsYetInGuildException(config, event));
        if (settings.getStatsDisabled()) {
            throw new YouHasDisableStatsException(config, event);
        }
        statsRepository.findByMember_DiscordIdAndGuild_DiscordId(event.getMemberId(), event.getGuildId())
            .ifPresentOrElse(
                memberStats -> {
                    final MessageEmbed messageEmbed = embedBuilder.createMemberStatsMessage(event, memberStats);
                    event.sendEmbedMessage(messageEmbed);
                },
                () -> { throw new YouHasNoStatsYetInGuildException(config, event); }
            );
    }
}
