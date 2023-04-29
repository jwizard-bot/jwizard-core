/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CacheableGuildSettingsDao.java
 * Last modified: 25/04/2023, 15:45
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
