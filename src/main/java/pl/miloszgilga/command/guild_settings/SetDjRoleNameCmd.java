/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetDjRoleNameCmd.java
 * Last modified: 17/05/2023, 14:57
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

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.HierarchyException;

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
import pl.miloszgilga.cacheable.CacheableGuildSettingsDao;
import pl.miloszgilga.command.AbstractGuildSettingsCommand;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import static pl.miloszgilga.exception.SettingsException.RoleAlreadyExistException;
import static pl.miloszgilga.exception.SettingsException.InsufficientPermissionRoleHierarchyException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class SetDjRoleNameCmd extends AbstractGuildSettingsCommand {

    SetDjRoleNameCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler,
        IGuildSettingsRepository repository, CacheableGuildSettingsDao cacheableGuildSettingsDao
    ) {
        super(BotCommand.SET_DJ_ROLE_NAME, config, embedBuilder, handler, repository, cacheableGuildSettingsDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteGuildSettingsCommand(CommandEventWrapper event) {
        final String djRoleName = event.getArgumentAndParse(BotCommandArgument.SET_DJ_ROLE_NAME_TAG);
        final String defDjRoleName = config.getProperty(BotProperty.J_DJ_ROLE_NAME);
        final String roleName = handler.getPossibleRemoteProperty(RemoteProperty.R_DJ_ROLE_NAME, event.getGuild());
        final List<Role> existingRoles = event.getGuild().getRolesByName(roleName, true);

        GuildSettingsEntity settingsToSave;
        MessageEmbed messageEmbed;
        try {
            if (Objects.isNull(djRoleName) || djRoleName.equals(StringUtils.EMPTY)) { // reset
                for (final Role role : existingRoles) {
                    role.getManager().setName(defDjRoleName).complete();
                }
                settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                    guildSettings -> guildSettings.setDjRoleName(null));
                messageEmbed = embedBuilder.createMessage(ResLocaleSet.DJ_ROLE_NAME_WAS_RESET_MESS, Map.of(
                    "setDjRoleNameCmd", BotCommand.SET_DJ_ROLE_NAME.parseWithPrefix(config)
                ), event.getGuild());
                JDALog.info(log, event, "DJ role name was successfully reset to '%s' (default value)", defDjRoleName);
            } else {
                final String abbreviateRole = djRoleName.substring(0, Math.min(djRoleName.length(), 20));
                if (!(event.getGuild().getRolesByName(abbreviateRole, true).isEmpty())) {
                    throw new RoleAlreadyExistException(config, event);
                }
                for (final Role role : existingRoles) {
                    role.getManager().setName(abbreviateRole).complete();
                }
                settingsToSave = cacheableGuildSettingsDao.setCacheableProperty(event,
                    guildSettings -> guildSettings.setDjRoleName(abbreviateRole));
                messageEmbed = embedBuilder.createMessage(ResLocaleSet.DJ_ROLE_NAME_WAS_SETTED_MESS, Map.of(
                    "djRoleName", djRoleName,
                    "setDjRoleNameCmd", BotCommand.SET_DJ_ROLE_NAME.parseWithPrefix(config)
                ), event.getGuild());
                JDALog.info(log, event, "DJ role name was successfully setted to '%s'", djRoleName);
            }
            repository.save(settingsToSave);
            event.sendEmbedMessage(messageEmbed);
        } catch (HierarchyException ex) {
            final String botRoleName = Objects.isNull(event.getGuild().getBotRole())
                ? StringUtils.EMPTY : event.getGuild().getBotRole().getName();
            throw new InsufficientPermissionRoleHierarchyException(config, event, botRoleName);
        }
    }
}
