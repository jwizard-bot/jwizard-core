/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RemotePropertyHandler.java
 * Last modified: 28/04/2023, 20:53
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

package pl.miloszgilga.core.remote;

import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.domain.guild_modules.GuildModulesEntity;
import pl.miloszgilga.domain.guild_modules.IGuildModulesRepository;
import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class RemotePropertyHandler {

    private final BotConfiguration config;
    private final IGuildModulesRepository modulesRepository;
    private final IGuildSettingsRepository settingsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    RemotePropertyHandler(
        BotConfiguration config, IGuildModulesRepository modulesRepository, IGuildSettingsRepository settingsRepository
    ) {
        this.config = config;
        this.modulesRepository = modulesRepository;
        this.settingsRepository = settingsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <T> T getPossibleRemoteProperty(RemoteProperty property, Guild guild, Class<T> castClazz) {
        try {
            final Optional<GuildSettingsEntity> settings = settingsRepository.findByGuild_DiscordId(guild.getId());
            if (settings.isEmpty()) throw new IllegalStateException();

            final Object remoteProp = property.getRemoteProp().apply(settings.get());
            if (Objects.isNull(property.getLocalProperty())) return null;
            if (Objects.isNull(remoteProp)) throw new IllegalStateException();

            return castClazz.cast(remoteProp);
        } catch (IllegalStateException ignore) {
            return config.getProperty(property.getLocalProperty(), castClazz);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean getPossibleRemoteModuleProperty(RemoteModuleProperty property, Guild guild) {
        try {
            final Optional<GuildModulesEntity> module = modulesRepository.findByGuild_DiscordId(guild.getId());
            if (module.isEmpty()) throw new IllegalStateException();

            final Boolean remoteProp = property.getRemoteProp().apply(module.get());
            if (Objects.isNull(remoteProp)) throw new IllegalStateException();

            return remoteProp;
        } catch (IllegalStateException ignore) {
            return config.getProperty(property.getLocalProperty(), Boolean.class);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BotConfiguration getConfig() {
        return config;
    }
}
