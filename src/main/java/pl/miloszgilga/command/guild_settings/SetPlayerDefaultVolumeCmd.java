/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetPlayerDefaultVolumeCmd.java
 * Last modified: 16/05/2023, 18:45
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

import static pl.miloszgilga.exception.AudioPlayerException.VolumeUnitsOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetPlayerDefaultVolumeCmd extends AbstractGuildSettingsCommand {

    SetPlayerDefaultVolumeCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao
    ) {
        super(BotCommand.SET_DEF_VOLUME, config, embedBuilder, handler, repository, cacheableGuildSettingsDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final Integer defaultVolume = event.getArgumentAndParse(BotCommandArgument.SET_DEF_VOLUME_TAG);
        final int defPlayerVolume = config.getProperty(BotProperty.J_DEFAULT_PLAYER_VOLUME_UNITS, Integer.class);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(defaultVolume)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setPlayerVolume(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.PLAYER_DEFAULT_VOLUME_WAS_RESET_MESS, Map.of(
                "setDefVolumeCmd", BotCommand.SET_DEF_VOLUME.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Player volume was successfully reset to '%s' (default value)", defPlayerVolume);
        } else {
            if (defaultVolume > 150 || defaultVolume < 0) {
                throw new VolumeUnitsOutOfBoundsException(config, event);
            }
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setPlayerVolume(defaultVolume));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.PLAYER_DEFAULT_VOLUME_WAS_SETTED_MESS, Map.of(
                "defVolume", defaultVolume,
                "setDefVolumeCmd", BotCommand.SET_DEF_VOLUME.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Player volume was successfully setted to '%s'", defaultVolume);
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
