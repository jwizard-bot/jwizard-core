/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: TurnOnStatsModuleCmd.java
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
import pl.miloszgilga.cacheable.CacheableGuildModulesDao;
import pl.miloszgilga.core.remote.RemoteModuleProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_modules.GuildModulesEntity;
import pl.miloszgilga.domain.guild_modules.IGuildModulesRepository;

import static pl.miloszgilga.exception.ModuleException.StatsModuleIsAlreadyRunningException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class TurnOnStatsModuleCmd extends AbstractOwnerCommand {

    private final IGuildModulesRepository modulesRepository;
    private final CacheableGuildModulesDao cacheableGuildModulesDao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    TurnOnStatsModuleCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildModulesRepository modulesRepository, CacheableGuildModulesDao cacheableGuildModulesDao
    ) {
        super(BotCommand.TURN_ON_STATS_MODULE, config, embedBuilder, handler);
        this.modulesRepository = modulesRepository;
        this.cacheableGuildModulesDao = cacheableGuildModulesDao;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteOwnerCommand(CommandEventWrapper event) {
        if (handler.getPossibleRemoteModuleProperty(RemoteModuleProperty.R_STATS_MODULE_ENABLED, event.getGuild())) {
            throw new StatsModuleIsAlreadyRunningException(config, event);
        }
        final CacheableModuleData data = CacheableModuleData.builder()
            .event(event)
            .settingModule(BotProperty.J_STATS_MODULE_ENABLED)
            .modulePropGetter(GuildModulesEntity::getStatsModuleEnabled)
            .modulePropSetter(GuildModulesEntity::setStatsModuleEnabled)
            .moduleRemoteSetter(modules -> modules.setStatsModuleEnabled(true))
            .build();

        final var updatedSettings = cacheableGuildModulesDao.toggleGuildModule(data);
        modulesRepository.save(updatedSettings);

        final MessageEmbed messageEmbed = embedBuilder.createMessage(ResLocaleSet.GUILD_STATS_MODULE_ENABLED_MESS, Map.of(
            "disableStatsModuleCmd", BotCommand.TURN_OFF_STATS_MODULE.parseWithPrefix(config)
        ), event.getGuild());
        JDALog.info(log, event, "Stats module for selected guild '%s' was successfully enabled", event.getGuildName());
        event.sendEmbedMessage(messageEmbed);
    }
}
