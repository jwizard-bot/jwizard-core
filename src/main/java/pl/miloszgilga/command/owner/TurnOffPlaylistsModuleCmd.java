/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: TurnOffPlaylistsModuleCmd.java
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

package pl.miloszgilga.command.owner;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractOwnerCommand;
import pl.miloszgilga.cacheable.CacheableModuleData;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.cacheable.CacheableGuildModulesDao;
import pl.miloszgilga.core.remote.RemoteModuleProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_modules.GuildModulesEntity;
import pl.miloszgilga.domain.guild_modules.IGuildModulesRepository;

import static pl.miloszgilga.exception.ModuleException.PlaylistsModuleIsAlreadyDisabledException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class TurnOffPlaylistsModuleCmd extends AbstractOwnerCommand {

    private final IGuildModulesRepository modulesRepository;
    private final CacheableGuildModulesDao cacheableGuildModulesDao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    TurnOffPlaylistsModuleCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IGuildModulesRepository modulesRepository,
        CacheableGuildModulesDao cacheableGuildModulesDao, RemotePropertyHandler handler,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.TURN_OFF_PLAYLISTS_MODULE, config, embedBuilder, handler, cacheableCommandStateDao);
        this.modulesRepository = modulesRepository;
        this.cacheableGuildModulesDao = cacheableGuildModulesDao;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteOwnerCommand(CommandEventWrapper event) {
        if (!handler.getPossibleRemoteModuleProperty(RemoteModuleProperty.R_PLAYLISTS_MODULE_ENABLED, event.getGuild())) {
            throw new PlaylistsModuleIsAlreadyDisabledException(config, event);
        }
        final CacheableModuleData data = CacheableModuleData.builder()
            .event(event)
            .settingModule(BotProperty.J_PLAYLISTS_MODULE_ENABLED)
            .modulePropGetter(GuildModulesEntity::getPlaylistsModuleEnabled)
            .modulePropSetter(GuildModulesEntity::setPlaylistsModuleEnabled)
            .moduleRemoteSetter(modules -> modules.setPlaylistsModuleEnabled(false))
            .build();

        final var updatedSettings = cacheableGuildModulesDao.toggleGuildModule(data);
        modulesRepository.save(updatedSettings);

        final MessageEmbed messageEmbed = embedBuilder.createMessage(ResLocaleSet.GUILD_PLAYLISTS_MODULE_DISABLED_MESS, Map.of(
            "enablePlaylistsModuleCmd", BotCommand.TURN_ON_PLAYLISTS_MODULE.parseWithPrefix(config)
        ), event.getGuild());
        JDALog.info(log, event, "Playlists module for selected guild '%s' was successfully disabled", event.getGuildName());
        event.sendEmbedMessage(messageEmbed);
    }
}
