/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CacheableMemberSettingsDao.java
 * Last modified: 25/04/2023, 15:59
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

import pl.miloszgilga.domain.member_settings.MemberSettingsEntity;
import pl.miloszgilga.domain.member_settings.IMemberSettingsRepository;

import static pl.miloszgilga.exception.StatsException.MemberHasNoStatsYetInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class CacheableMemberSettingsDao extends AbstractCacheableDao<MemberSettingsEntity, IMemberSettingsRepository> {

    CacheableMemberSettingsDao(BotConfiguration config, IMemberSettingsRepository settingsRepository) {
        super(config, settingsRepository);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "MemberSettingsCache", key = "#p0.member.id.concat(#p0.guild.id)")
    public MemberSettingsEntity toggleStatsVisibility(CommandEventWrapper event, boolean isPrivate, Consumer<Boolean> exec) {
        final MemberSettingsEntity settings = cacheableRepository
            .findByMember_DiscordIdAndGuild_DiscordId(event.getMemberId(), event.getGuildId())
            .orElseThrow(() -> new MemberHasNoStatsYetInGuildException(config, event, event.getMember().getUser()));

        exec.accept(settings.getStatsPrivate());
        settings.setStatsPrivate(isPrivate);
        return settings;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "MemberSettingsCache", key = "#p0.member.id.concat(#p0.guild.id)")
    public MemberSettingsEntity toggleMemberStatsDisabled(CommandEventWrapper event, boolean isDisabled, Consumer<Boolean> exec) {
        final MemberSettingsEntity settings = cacheableRepository
            .findByMember_DiscordIdAndGuild_DiscordId(event.getMemberId(), event.getGuildId())
            .orElseThrow(() -> new MemberHasNoStatsYetInGuildException(config, event, event.getMember().getUser()));

        exec.accept(settings.getStatsDisabled());
        settings.setStatsDisabled(isDisabled);
        return settings;
    }
}
