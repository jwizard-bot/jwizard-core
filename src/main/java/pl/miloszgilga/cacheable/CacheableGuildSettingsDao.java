/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CacheableGuildSettingsDao.java
 * Last modified: 16/05/2023, 13:03
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

package pl.miloszgilga.cacheable;

import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.CachePut;

import java.util.function.Consumer;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.core.configuration.BotConfiguration;

import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import static pl.miloszgilga.exception.CommandException.UnexpectedException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class CacheableGuildSettingsDao extends AbstractCacheableDao<GuildSettingsEntity, IGuildSettingsRepository> {

    CacheableGuildSettingsDao(BotConfiguration config, IGuildSettingsRepository settingsRepository) {
        super(config, settingsRepository);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "GuildSettingsCache", key = "#p0.guild.id")
    public GuildSettingsEntity setCacheableProperty(CommandEventWrapper event, Consumer<GuildSettingsEntity> performAction) {
        final GuildSettingsEntity settings = cacheableRepository.findByGuild_DiscordId(event.getGuildId())
            .orElseThrow(() -> new UnexpectedException(config, event));

        performAction.accept(settings);
        return settings;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "GuildSettingsCache", key = "#p0")
    public GuildSettingsEntity deleteMusicBotTextChannelOnRemoving(String guildId, String deletingChannelId) {
        final GuildSettingsEntity settings = cacheableRepository.findByGuild_DiscordId(guildId)
            .orElseThrow(() -> new UnexpectedException(config, null));
        if (settings.getAudioTextChannelId().equals(deletingChannelId)) {
            settings.setAudioTextChannelId(null);
        }
        return settings;
    }
}
