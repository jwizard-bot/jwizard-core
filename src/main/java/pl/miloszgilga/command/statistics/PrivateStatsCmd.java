/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PrivateStatsCmd.java
 * Last modified: 16/05/2023, 18:52
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

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMyStatsCommand;
import pl.miloszgilga.cacheable.CacheableMemberSettingsDao;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;
import pl.miloszgilga.domain.member_settings.IMemberSettingsRepository;

import static pl.miloszgilga.exception.StatsException.StatsAlreadyPrivateException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class PrivateStatsCmd extends AbstractMyStatsCommand {

    private final CacheableMemberSettingsDao cacheableMemberSettingsDao;
    private final IMemberSettingsRepository settingsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    PrivateStatsCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository repository,
        CacheableMemberSettingsDao cacheableMemberSettingsDao, IMemberSettingsRepository settingsRepository,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.PRIVATE_STATS, config, embedBuilder, repository, handler);
        this.cacheableMemberSettingsDao = cacheableMemberSettingsDao;
        this.settingsRepository = settingsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMyStatsCommand(CommandEventWrapper event) {
        final var updatedSettings = cacheableMemberSettingsDao.toggleStatsVisibility(event, true, isPrivate -> {
            if (isPrivate) throw new StatsAlreadyPrivateException(config, event);
        });
        settingsRepository.save(updatedSettings);
        final String userTag = event.getMember().getUser().getAsTag();
        final MessageEmbed messageEmbed = embedBuilder.createMessage(ResLocaleSet.SET_STATS_TO_PRIVATE_MESS, Map.of(
            "statsPublicCmd", BotCommand.PUBLIC_STATS.parseWithPrefix(config)
        ), event.getGuild());
        JDALog.info(log, event, "Stats for selected memeber '%s' was successfully set to private", userTag);
        event.sendEmbedMessage(messageEmbed);
    }
}
