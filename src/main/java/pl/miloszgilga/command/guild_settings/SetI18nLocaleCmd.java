/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetI18nLocaleCmd.java
 * Last modified: 17/05/2023, 01:55
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
import java.util.List;
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
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import static pl.miloszgilga.exception.SettingsException.LocaleNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetI18nLocaleCmd extends AbstractGuildSettingsCommand {

    SetI18nLocaleCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.SET_I18N_LOCALE, config, embedBuilder, handler, repository, cacheableGuildSettingsDao,
            cacheableCommandStateDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final String i18locale = event.getArgumentAndParse(BotCommandArgument.SET_I18N_LOCALE_TAG);
        final String defLocale = config.getProperty(BotProperty.J_SELECTED_LOCALE);
        final String availableLocales = config.getProperty(BotProperty.J_AVAILABLE_LOCALES);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;

        if (Objects.isNull(i18locale) || i18locale.equals(StringUtils.EMPTY)) { // reset
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setI18nLocale(null));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.I18N_LOCALE_WAS_RESET_MESS, Map.of(
                "setI18nLocaleCmd", BotCommand.SET_I18N_LOCALE.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "i18n locale was successfully reset to '%s' (default value)", defLocale);
        } else {
            final List<String> avLocales = List.of(availableLocales.split(","));
            if (avLocales.stream().noneMatch(l -> l.equals(i18locale))) {
                throw new LocaleNotFoundException(config, event);
            }
            settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                guildSettings -> guildSettings.setI18nLocale(i18locale));
            messageEmbed = embedBuilder.createMessage(ResLocaleSet.I18N_LOCALE_WAS_SETTED_MESS, Map.of(
                "i18nLocale", i18locale,
                "setI18nLocaleCmd", BotCommand.SET_I18N_LOCALE.parseWithPrefix(config)
            ), event.getGuild());
            JDALog.info(log, event, "i18n locale was successfully setted to '%s'", i18locale);
        }
        repository.save(settingsToSave);
        event.sendEmbedMessage(messageEmbed);
    }
}
