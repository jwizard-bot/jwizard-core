/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetMaxRepeatsSingleTrackCmd.java
 * Last modified: 16/05/2023, 18:43
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

package pl.miloszgilga.command.guild_settings;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;
import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.cacheable.CacheableGuildSettingsDao;
import pl.miloszgilga.command.AbstractGuildSettingsCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import static pl.miloszgilga.exception.SettingsException.MaxRepeatsOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetMaxRepeatsSingleTrackCmd extends AbstractGuildSettingsCommand {

    SetMaxRepeatsSingleTrackCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao
    ) {
        super(BotCommand.SET_TRACK_REPEATS, config, embedBuilder, handler, repository, cacheableGuildSettingsDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final Integer maxRepeats = event.getArgumentAndParse(BotCommandArgument.SET_TRACK_REPEATS_TAG);
        final int defMaxRepeats = config.getProperty(BotProperty.J_MAX_REPEATS_SINGLE_TRACK, Integer.class);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(maxRepeats)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setMaxRepeatsSingleTrack(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.MAX_REPEATS_SINGLE_TRACK_WAS_RESET_MESS, Map.of(
                "setTrackRepeatsCmd", BotCommand.SET_TRACK_REPEATS.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Max repeats of single track was successfully reset to '%s' (default value)",
                defMaxRepeats);
        } else {
            if (maxRepeats < 1 || maxRepeats > defMaxRepeats) {
                throw new MaxRepeatsOutOfBoundsException(config, event);
            }
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setMaxRepeatsSingleTrack(maxRepeats));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.MAX_REPEATS_SINGLE_TRACK_WAS_SETTED_MESS, Map.of(
                "maxRepeats", maxRepeats,
                "setTrackRepeatsCmd", BotCommand.SET_TRACK_REPEATS.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Max repeats of single track was successfully setted to '%s'", maxRepeats);
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
