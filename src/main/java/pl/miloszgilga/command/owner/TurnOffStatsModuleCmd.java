/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: TurnOffStatsModuleCmd.java
 * Last modified: 28/04/2023, 22:06
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

import static pl.miloszgilga.exception.ModuleException.StatsModuleIsAlreadyDisabledException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class TurnOffStatsModuleCmd extends AbstractOwnerCommand {

    private final IGuildModulesRepository modulesRepository;
    private final CacheableGuildModulesDao cacheableGuildModulesDao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    TurnOffStatsModuleCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IGuildModulesRepository modulesRepository,
        CacheableGuildModulesDao cacheableGuildModulesDao, RemotePropertyHandler handler
    ) {
        super(BotCommand.TURN_OFF_STATS_MODULE, config, embedBuilder, handler);
        this.modulesRepository = modulesRepository;
        this.cacheableGuildModulesDao = cacheableGuildModulesDao;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteOwnerCommand(CommandEventWrapper event) {
        if (!handler.getPossibleRemoteModuleProperty(RemoteModuleProperty.R_STATS_MODULE_ENABLED, event.getGuild())) {
            throw new StatsModuleIsAlreadyDisabledException(config, event);
        }
        final CacheableModuleData data = CacheableModuleData.builder()
            .event(event)
            .settingModule(BotProperty.J_STATS_MODULE_ENABLED)
            .modulePropGetter(GuildModulesEntity::getStatsModuleEnabled)
            .modulePropSetter(GuildModulesEntity::setStatsModuleEnabled)
            .moduleRemoteSetter(modules -> modules.setStatsModuleEnabled(false))
            .build();

        final var updatedSettings = cacheableGuildModulesDao.toggleGuildModule(data);
        modulesRepository.save(updatedSettings);

        final MessageEmbed messageEmbed = embedBuilder.createMessage(ResLocaleSet.GUILD_STATS_MODULE_DISABLED_MESS, Map.of(
            "enableStatsModuleCmd", BotCommand.TURN_ON_STATS_MODULE.parseWithPrefix(config)
        ));
        JDALog.info(log, event, "Stats module for selected guild '%s' was successfully disabled", event.getGuildName());
        event.sendEmbedMessage(messageEmbed);
    }
}
