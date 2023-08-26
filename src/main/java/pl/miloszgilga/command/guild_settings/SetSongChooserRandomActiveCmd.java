/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetSongChooserRandomActiveCmd.java
 * Last modified: 18/06/2023, 18:18
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
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.cacheable.CacheableGuildSettingsDao;
import pl.miloszgilga.command.AbstractGuildSettingsCommand;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetSongChooserRandomActiveCmd extends AbstractGuildSettingsCommand {

    SetSongChooserRandomActiveCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.SET_RANDOM_CHOOSE_SONG, config, embedBuilder, handler, repository, cacheableGuildSettingsDao,
            cacheableCommandStateDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final Boolean selectSongRandom = event.getArgumentAndParse(BotCommandArgument.SET_RANDOM_CHOOSE_SONG_TAG);
        final boolean defSelectSongRandom = config.getProperty(BotProperty.J_SONG_CHOOSER_RANDOM_ACTIVE, Boolean.class);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(selectSongRandom)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setSongSelectIsRandom(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.SONG_CHOOSER_RANDOM_ACTIVE_WAS_RESET_MESS, Map.of(
                "setSongChooserRandomActiveCmd", BotCommand.SET_RANDOM_CHOOSE_SONG.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Random choosing song property was successfully reset to '%s' (default value)",
                defSelectSongRandom);
        } else {
            final String turnOn = config.getLocaleText(ResLocaleSet.TURN_ON_MESS, event.getGuild());
            final String turnOff = config.getLocaleText(ResLocaleSet.TURN_OFF_MESS, event.getGuild());

            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setSongSelectIsRandom(selectSongRandom));

            messageEmbed = embedBuilder.createMessage(ResLocaleSet.SONG_CHOOSER_RANDOM_ACTIVE_WAS_SETTED_MESS, Map.of(
                "randomActive", selectSongRandom ? turnOn : turnOff,
                "setSongChooserRandomActiveCmd", BotCommand.SET_RANDOM_CHOOSE_SONG.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Random choosing song property was successfully setted to '%s'", selectSongRandom);
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
