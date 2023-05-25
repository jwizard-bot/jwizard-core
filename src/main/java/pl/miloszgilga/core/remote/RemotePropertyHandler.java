/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RemotePropertyHandler.java
 * Last modified: 15/05/2023, 17:29
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
            if (Objects.isNull(remoteProp) && !property.isHasLocalProperty()) return null;
            if (Objects.isNull(remoteProp)) throw new IllegalStateException();

            return castClazz.cast(remoteProp);
        } catch (IllegalStateException ignore) {
            return config.getProperty(property.getLocalProperty(), castClazz);
        }
    }

    public String getPossibleRemoteProperty(RemoteProperty property, Guild guild) {
        return getPossibleRemoteProperty(property, guild, String.class);
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
