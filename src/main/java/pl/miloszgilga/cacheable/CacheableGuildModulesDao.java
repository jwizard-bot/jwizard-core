/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CacheableGuildModulesDao.java
 * Last modified: 29/04/2023, 01:24
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

import java.util.Objects;

import pl.miloszgilga.core.configuration.BotConfiguration;

import pl.miloszgilga.domain.guild_modules.GuildModulesEntity;
import pl.miloszgilga.domain.guild_modules.IGuildModulesRepository;

import static pl.miloszgilga.exception.CommandException.UnexpectedException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class CacheableGuildModulesDao extends AbstractCacheableDao<GuildModulesEntity, IGuildModulesRepository> {

    CacheableGuildModulesDao(BotConfiguration config, IGuildModulesRepository modulesRepository) {
        super(config, modulesRepository);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "GuildModulesCache", key = "#p0.event.guild.id")
    public GuildModulesEntity toggleGuildModule(CacheableModuleData data) {
        final GuildModulesEntity modules = cacheableRepository
            .findByGuild_DiscordId(data.event().getGuildId())
            .orElseThrow(() -> new UnexpectedException(config, data.event()));

        final Boolean propState = data.modulePropGetter().apply(modules);
        if (Objects.isNull(propState)) {
            final boolean settingProp = config.getProperty(data.settingModule(), Boolean.class);
            data.modulePropSetter().accept(modules, settingProp);
        }
        data.moduleRemoteSetter().accept(modules);
        return modules;
    }
}
