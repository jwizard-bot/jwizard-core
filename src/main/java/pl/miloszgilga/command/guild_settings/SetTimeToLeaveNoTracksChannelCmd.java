/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetTimeToLeaveNoTracksChannelCmd.java
 * Last modified: 17/05/2023, 01:13
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

import static pl.miloszgilga.exception.SettingsException.TimeToLeaveNoTracksOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetTimeToLeaveNoTracksChannelCmd extends AbstractGuildSettingsCommand {

    SetTimeToLeaveNoTracksChannelCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao
    ) {
        super(BotCommand.SET_TIME_LEAVE_NO_TRACKS, config, embedBuilder, handler, repository, cacheableGuildSettingsDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final Integer timeToLeave = event.getArgumentAndParse(BotCommandArgument.SET_TIME_LEAVE_NO_TRACKS_TAG);

        final int defTimeToLeave = config.getProperty(BotProperty.J_INACTIVITY_NO_TRACK_TIMEOUT, Integer.class);
        final int maxTimeToLeave = config.getProperty(BotProperty.J_MAX_INACTIVITY_NO_TRACK_TIME, Integer.class);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(timeToLeave)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setTimeToLeaveNoTracksChannel(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.TIME_TO_LEAVE_NO_TRACKS_WAS_RESET_MESS, Map.of(
                "setTimeLeaveNoTracksCmd", BotCommand.SET_TIME_LEAVE_NO_TRACKS.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Time after leave no tracks left was successfully reset to '%s' (default value)",
                defTimeToLeave);
        } else {
            if (timeToLeave < 5 || timeToLeave > maxTimeToLeave) {
                throw new TimeToLeaveNoTracksOutOfBoundsException(config, event);
            }
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setTimeToLeaveNoTracksChannel(timeToLeave));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.TIME_TO_LEAVE_NO_TRACKS_WAS_SETTED_MESS, Map.of(
                "timeToLeave", timeToLeave,
                "setTimeLeaveNoTracksCmd", BotCommand.SET_TIME_LEAVE_NO_TRACKS.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Time after leave no tracks left was successfully setted to '%s'", timeToLeave);
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
