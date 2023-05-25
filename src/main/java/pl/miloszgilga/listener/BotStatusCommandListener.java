/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotStatusCommandListener.java
 * Last modified: 17/05/2023, 14:49
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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

import java.util.List;
import java.util.Collections;
import java.util.stream.Stream;

import org.springframework.transaction.annotation.Transactional;
import pl.miloszgilga.embed.EmbedColor;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.AloneOnChannelListener;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

import pl.miloszgilga.domain.guild.IGuildRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableListenerLazyService
public class BotStatusCommandListener extends AbstractListenerAdapter {

    private boolean shuttingDown = false;

    private final AloneOnChannelListener aloneListener;
    private final PlayerManager playerManager;
    private final RemotePropertyHandler handler;
    private final IGuildRepository guildRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BotStatusCommandListener(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, AloneOnChannelListener aloneListener,
        PlayerManager playerManager, RemotePropertyHandler handler, IGuildRepository guildRepository
    ) {
        super(config, embedBuilder);
        this.aloneListener = aloneListener;
        this.playerManager = playerManager;
        this.handler = handler;
        this.guildRepository = guildRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setBotDeafen(GuildVoiceJoinEvent event) {
        if (!event.getMember().getUser().isBot()) return;
        event.getGuild().getAudioManager().setSelfDeafened(true);
        event.getGuild().getSelfMember().deafen(true).complete();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void removeUnusedTables(ReadyEvent event) {
        final List<String> guildDcIds = event.getJDA().getGuilds().stream().map(ISnowflake::getId).toList();
        final List<String> guildDcIdsInDb = guildRepository.findAllGuilds();

        final List<String> guildsToDelete = Stream.concat(guildDcIds.stream(), guildDcIdsInDb.stream())
            .filter(item -> Collections.frequency(guildDcIds, item) + Collections.frequency(guildDcIdsInDb, item) == 1)
            .toList();
        for (final String guildId : guildsToDelete) {
            guildRepository.deleteGuildEntityByDiscordId(guildId);
            log.info("Remove unused guild tables from guild '{}'", guildId);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void shutdownBotInstance(ShutdownEvent event) {
        if (shuttingDown) return;
        shuttingDown = true;
        super.config.getThreadPool().shutdownNow();
        if (event.getJDA().getStatus() == JDA.Status.SHUTTING_DOWN) return;

        for (final Guild guild : event.getJDA().getGuilds()) {
            guild.getAudioManager().closeAudioConnection();
            playerManager.getMusicManager(guild).getActions().clearAndDestroy(false);
        }
        event.getJDA().shutdown();
        System.exit(0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void createDjRoleOnDelete(RoleDeleteEvent event) {
        final Role deletedRole = event.getRole();
        final Guild guild = event.getGuild();
        final String defaultDjRoleName = handler.getPossibleRemoteProperty(RemoteProperty.R_DJ_ROLE_NAME, guild);

        if (!deletedRole.getName().equals(defaultDjRoleName)) return;
        guild.createRole()
            .setName(defaultDjRoleName)
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .submit();
        log.info("Re-created removed DJ role '{}' for guild '{}'", defaultDjRoleName, guild.getName());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Transactional @Override public void onReady(ReadyEvent event)          { removeUnusedTables(event); }
    @Override public void onRoleDelete(RoleDeleteEvent event)               { createDjRoleOnDelete(event); }
    @Override public void onGuildVoiceJoin(GuildVoiceJoinEvent event)       { setBotDeafen(event); }
    @Override public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event)   { aloneListener.onEveryVoiceUpdate(event); }
    @Override public void onShutdown(ShutdownEvent event)                   { shutdownBotInstance(event); }
}
