/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetTimeToEndVotingCmd.java
 * Last modified: 16/05/2023, 18:42
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
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import static pl.miloszgilga.exception.SettingsException.TimeToEndVotingOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetTimeToEndVotingCmd extends AbstractGuildSettingsCommand {

    SetTimeToEndVotingCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.SET_TIME_VOTING, config, embedBuilder, handler, repository, cacheableGuildSettingsDao,
            cacheableCommandStateDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final Integer timeToEndVoting = event.getArgumentAndParse(BotCommandArgument.SET_TIME_VOTING_TAG);

        final int defTimeToEndVoting = config.getProperty(BotProperty.J_INACTIVITY_VOTING_TIMEOUT, Integer.class);
        final int maxTimeToEndVoting = config.getProperty(BotProperty.J_MAX_INACTIVITY_VOTING_TIME, Integer.class);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(timeToEndVoting)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setTimeToEndVoting(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.TIME_TO_END_VOTING_WAS_RESET_MESS, Map.of(
                "setTimeVotingCmd", BotCommand.SET_TIME_VOTING.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Time to end voting was successfully reset to '%s' (default value)",
                defTimeToEndVoting);
        } else {
            if (timeToEndVoting < 5 || timeToEndVoting > maxTimeToEndVoting) {
                throw new TimeToEndVotingOutOfBoundsException(config, event);
            }
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setTimeToLeaveNoTracksChannel(timeToEndVoting));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.TIME_TO_END_VOTING_WAS_SETTED_MESS, Map.of(
                "timeToEndVoting", timeToEndVoting,
                "setTimeVotingCmd", BotCommand.SET_TIME_VOTING.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "Time to end voting was successfully setted to '%s'", timeToEndVoting);
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
