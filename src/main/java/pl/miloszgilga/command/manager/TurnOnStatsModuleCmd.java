/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: TurnOnStatsModuleCmd.java
 * Last modified: 10/04/2023, 12:32
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

package pl.miloszgilga.command.manager;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractManagerCommand;
import pl.miloszgilga.cacheable.CacheableGuildSettingsDao;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import static pl.miloszgilga.exception.StatsException.StatsModuleIsAlreadyRunningException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class TurnOnStatsModuleCmd extends AbstractManagerCommand {

    private final IGuildSettingsRepository settingsRepository;
    private final CacheableGuildSettingsDao cacheableGuildSettingsDao;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    TurnOnStatsModuleCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IGuildSettingsRepository settingsRepository,
        CacheableGuildSettingsDao cacheableGuildSettingsDao
    ) {
        super(BotCommand.TURN_ON_STATS_MODULE, config, embedBuilder);
        this.settingsRepository = settingsRepository;
        this.cacheableGuildSettingsDao = cacheableGuildSettingsDao;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteManagerCommand(CommandEventWrapper event) {
        final var updatedSettings = cacheableGuildSettingsDao.toggleGuildStatisticsModule(event, true, isActive -> {
            if (isActive) throw new StatsModuleIsAlreadyRunningException(config, event);
        });
        settingsRepository.save(updatedSettings);
        final MessageEmbed messageEmbed = embedBuilder.createMessage(ResLocaleSet.GUILD_STATS_MODULE_ENABLED_MESS, Map.of(
            "disableStatsModuleCmd", BotCommand.TURN_OFF_STATS_MODULE.parseWithPrefix(config)
        ));
        JDALog.info(log, event, "Stats module for selected guild '%s' was successfully enabled", event.getGuildName());
        event.sendEmbedMessage(messageEmbed);
    }
}
