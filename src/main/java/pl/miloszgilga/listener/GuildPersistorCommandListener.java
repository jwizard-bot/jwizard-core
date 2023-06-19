/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildPersistorCommandListener.java
 * Last modified: 17/05/2023, 15:00
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

package pl.miloszgilga.listener;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;

import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import pl.miloszgilga.embed.EmbedColor;
import pl.miloszgilga.embed.EmbedInteractionsOnJoin;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.cacheable.CacheableGuildSettingsDao;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

import pl.miloszgilga.domain.guild.GuildEntity;
import pl.miloszgilga.domain.guild.IGuildRepository;
import pl.miloszgilga.domain.guild_stats.GuildStatsEntity;
import pl.miloszgilga.domain.guild_modules.GuildModulesEntity;
import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.guild_settings.IGuildSettingsRepository;

import pl.miloszgilga.domain.vote_commands.VoteCommandEntity;
import pl.miloszgilga.domain.dj_commands.DjCommandEntity;
import pl.miloszgilga.domain.stats_commands.StatsCommandEntity;
import pl.miloszgilga.domain.owner_commands.OwnerCommandEntity;
import pl.miloszgilga.domain.music_commands.MusicCommandEntity;
import pl.miloszgilga.domain.other_commands.OtherCommandEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableListenerLazyService
public class GuildPersistorCommandListener extends AbstractListenerAdapter {

    private final IGuildRepository guildRepository;
    private final IGuildSettingsRepository settingsRepository;
    private final CacheableGuildSettingsDao cacheableGuildSettingsDao;
    private final EmbedInteractionsOnJoin embedInteractionsOnJoin;
    private final RemotePropertyHandler handler;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    GuildPersistorCommandListener(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IGuildRepository guildRepository,
        IGuildSettingsRepository settingsRepository, CacheableGuildSettingsDao cacheableGuildSettingsDao,
        EmbedInteractionsOnJoin embedInteractionsOnJoin, RemotePropertyHandler handler
    ) {
        super(config, embedBuilder);
        this.guildRepository = guildRepository;
        this.settingsRepository = settingsRepository;
        this.cacheableGuildSettingsDao = cacheableGuildSettingsDao;
        this.embedInteractionsOnJoin = embedInteractionsOnJoin;
        this.handler = handler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void createOnlyIfGuildTableNotExist(GenericGuildEvent event) {
        if (!guildRepository.guildEntityExist(event.getGuild().getId())) {
            final GuildEntity guild = new GuildEntity(event.getGuild());
            guild.persistGuildStats(new GuildStatsEntity());
            guild.persistGuildSettings(new GuildSettingsEntity());
            guild.persistGuildModules(new GuildModulesEntity());

            guild.persistVoteCommand(new VoteCommandEntity());
            guild.persistStatsCommand(new StatsCommandEntity());
            guild.persistDjCommand(new DjCommandEntity());
            guild.persistOwnerCommand(new OwnerCommandEntity());
            guild.persistMusicCommand(new MusicCommandEntity());
            guild.persistOtherCommand(new OtherCommandEntity());

            guildRepository.save(guild);
        }
        embedInteractionsOnJoin.addInitialInteractions(event.getGuild());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void createDjRole(GenericGuildEvent event) {
        final Guild guild = event.getGuild();
        final String defaultDjRoleName = handler.getPossibleRemoteProperty(RemoteProperty.R_DJ_ROLE_NAME, guild);
        final List<Role> djRoles = event.getGuild().getRolesByName(defaultDjRoleName, false);
        if (djRoles.isEmpty()) {
            final Role djRole = guild.createRole()
                .setName(defaultDjRoleName)
                .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
                .complete();
            guild.modifyRolePositions().selectPosition(djRole).moveTo(0).queue();
            log.info("Create and modified DJ role position for guild '{}'", guild.getName());
        }
        createOnlyIfGuildTableNotExist(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void deleteGuildTables(GenericGuildEvent event) {
        guildRepository.deleteGuildEntityByDiscordId(event.getGuild().getId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateGuildName(GuildUpdateNameEvent event) {
        guildRepository.findByDiscordId(event.getGuild().getId()).ifPresent(guild -> {
            guild.setName(event.getNewName());
            guildRepository.save(guild);
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void deleteTextChannel(TextChannelDeleteEvent event) {
        final TextChannel removedChannel = event.getChannel();
        final GuildSettingsEntity settings = cacheableGuildSettingsDao
            .deleteMusicBotTextChannelOnRemoving(event.getGuild().getId(), removedChannel.getId());
        settingsRepository.save(settings);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void onGuildUpdateName(GuildUpdateNameEvent event)         { updateGuildName(event); }
    @Override public void onGuildReady(GuildReadyEvent event)                   { createOnlyIfGuildTableNotExist(event); }
    @Override public void onGuildJoin(GuildJoinEvent event)                     { createDjRole(event); }
    @Override public void onTextChannelDelete(TextChannelDeleteEvent event)     { deleteTextChannel(event); }
    @Transactional @Override public void onGuildLeave(GuildLeaveEvent event)    { deleteGuildTables(event); }
    @Transactional @Override public void onGuildBan(GuildBanEvent event)        { deleteGuildTables(event); }
}
