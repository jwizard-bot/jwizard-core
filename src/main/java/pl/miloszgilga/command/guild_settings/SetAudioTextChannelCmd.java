/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetAudioTextChannelCmd.java
 * Last modified: 15/05/2023, 14:27
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
import pl.miloszgilga.cacheable.CacheableGuildSettingsDao;
import pl.miloszgilga.command.AbstractGuildSettingsCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import static pl.miloszgilga.exception.CommandException.ChannelIsNotTextChannelException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetAudioTextChannelCmd extends AbstractGuildSettingsCommand {

    SetAudioTextChannelCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao
    ) {
        super(BotCommand.SET_AUDIO_CHANNEL, config, embedBuilder, handler, repository, cacheableGuildSettingsDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final String channel = event.getArgumentAndParse(BotCommandArgument.SET_AUDIO_TEXT_CHANNEL_TAG);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (channel.equals(StringUtils.EMPTY)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setMusicBotTextChannel(event, null);
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
            settingsToSave = cacheableGuildSettingsDao.setMusicBotTextChannel(event, filtered);
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
