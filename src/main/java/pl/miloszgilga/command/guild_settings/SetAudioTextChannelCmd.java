/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetAudioTextChannelCmd.java
 * Last modified: 17/05/2023, 01:34
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

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.cacheable.CacheableGuildSettingsDao;
import pl.miloszgilga.command.AbstractGuildSettingsCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import static pl.miloszgilga.exception.SettingsException.ChannelIsNotTextChannelException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetAudioTextChannelCmd extends AbstractGuildSettingsCommand {

    SetAudioTextChannelCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.SET_AUDIO_CHANNEL, config, embedBuilder, handler, repository, cacheableGuildSettingsDao,
            cacheableCommandStateDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final String channel = event.getArgumentAndParse(BotCommandArgument.SET_AUDIO_TEXT_CHANNEL_TAG);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(channel) || channel.equals(StringUtils.EMPTY)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setAudioTextChannelId(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.AUDIO_CHANNEL_WAS_RESET_MESS, Map.of(
                "setTextChannelCmd", BotCommand.SET_AUDIO_CHANNEL.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Text channel for song request module was successfully reset");
        } else {
            final String filtered = channel.replaceAll("\\D", StringUtils.EMPTY);
            final TextChannel textChannel = event.getGuild().getTextChannelById(filtered);
            if (Objects.isNull(textChannel) || !textChannel.getType().equals(ChannelType.TEXT)) {
                throw new ChannelIsNotTextChannelException(config, event);
            }
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setAudioTextChannelId(filtered));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.AUDIO_CHANNEL_WAS_SETTED_MESS, Map.of(
                "channelName", textChannel.getName(),
                "setTextChannelCmd", BotCommand.SET_AUDIO_CHANNEL.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Text channel for song request module was successfully setted: '%s'",
                textChannel.getName());
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
