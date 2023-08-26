/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetSongChooserVotingTimeCmd.java
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

import static pl.miloszgilga.exception.SettingsException.SongChooserTimeOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetSongChooserTimeCmd extends AbstractGuildSettingsCommand {

    SetSongChooserTimeCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.SET_TIME_CHOOSE_SONG, config, embedBuilder, handler, repository, cacheableGuildSettingsDao,
            cacheableCommandStateDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final Integer chooseTime = event.getArgumentAndParse(BotCommandArgument.SET_TIME_CHOOSE_SONG_TAG);

        final int maxChooseTime = config.getProperty(BotProperty.J_MAX_SONG_CHOOSER_SELECT_TIME, Integer.class);
        final int defChooseTime = config.getProperty(BotProperty.J_SONG_CHOOSER_SELECT_TIME, Integer.class);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(chooseTime)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setTimeToSelectSong(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.SONG_CHOOSER_TIME_WAS_RESET_MESS, Map.of(
                "setSongChooserTimeCmd", BotCommand.SET_TIME_CHOOSE_SONG.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Time to select song from results was successfully reset to '%s' (default value)",
                defChooseTime);
        } else {
            if (chooseTime < 6 || chooseTime > maxChooseTime) {
                throw new SongChooserTimeOutOfBoundsException(config, event);
            }
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setTimeToSelectSong(chooseTime));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.SONG_CHOOSER_TIME_WAS_SETTED_MESS, Map.of(
                "time", chooseTime,
                "setSongChooserTimeCmd", BotCommand.SET_TIME_CHOOSE_SONG.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Time to select song from results was successfully setted to '%s'", chooseTime);
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
