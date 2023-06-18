/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetSongChooserCountCmd.java
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

import static pl.miloszgilga.exception.SettingsException.SongChooserCountOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetSongChooserCountCmd extends AbstractGuildSettingsCommand {

    SetSongChooserCountCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.SET_COUNT_CHOOSE_SONG, config, embedBuilder, handler, repository, cacheableGuildSettingsDao,
            cacheableCommandStateDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final Integer songsCount = event.getArgumentAndParse(BotCommandArgument.SET_COUNT_CHOOSE_SONG_TAG);
        final int defSongsCount = config.getProperty(BotProperty.J_SONG_CHOOSER_COUNT, Integer.class);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(songsCount)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setSongChoicesCount(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.SONG_CHOOSER_COUNT_WAS_RESET_MESS, Map.of(
                "setSongChooserCountCmd", BotCommand.SET_COUNT_CHOOSE_SONG.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Count of songs showing as search results was successfully reset to '%s' (default value)",
                defSongsCount);
        } else {
            if (songsCount < 2 || songsCount > 10) {
                throw new SongChooserCountOutOfBoundsException(config, event);
            }
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setSongChoicesCount(songsCount));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.SONG_CHOOSER_COUNT_WAS_SETTED_MESS, Map.of(
                "count", songsCount,
                "setSongChooserCountCmd", BotCommand.SET_COUNT_CHOOSE_SONG.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Count of songs showing as search results was successfully setted to '%s'", songsCount);
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
