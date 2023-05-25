/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AloneOnChannelListener.java
 * Last modified: 16/05/2023, 20:25
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

package pl.miloszgilga.audioplayer;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class AloneOnChannelListener {

    private final BotConfiguration config;
    private final RemotePropertyHandler handler;
    private final PlayerManager playerManager;

    private JDA jda;
    private final Map<Long, Instant> aloneFromTime = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    AloneOnChannelListener(BotConfiguration config, RemotePropertyHandler handler, PlayerManager playerManager) {
        this.config = config;
        this.handler = handler;
        this.playerManager = playerManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initialize(JDA jda) {
        this.jda = jda;
        config.getThreadPool().scheduleWithFixedDelay(this::coccurentAloneCheck, 0, 5, TimeUnit.SECONDS);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onEveryVoiceUpdate(GuildVoiceUpdateEvent event) {
        final Integer maxInactivity = handler.getPossibleRemoteProperty(RemoteProperty.R_INACTIVITY_EMPTY_TIMEOUT,
            event.getGuild(), Integer.class);
        if (maxInactivity < 1) return;

        final Guild guild = event.getGuild();
        if (Objects.isNull(guild.getAudioManager().getSendingHandler())) return;

        final boolean isAlone = isAloneOnChannel(guild);
        final boolean isAlonePrevious = aloneFromTime.containsKey(guild.getIdLong());

        if (!isAlone && isAlonePrevious) {
            aloneFromTime.remove(guild.getIdLong());
        } else if (isAlone && !isAlonePrevious) {
            aloneFromTime.put(guild.getIdLong(), Instant.now());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void coccurentAloneCheck() {
        final Set<Long> removeFromGuild = new HashSet<>();
        for (final Map.Entry<Long, Instant> entry : aloneFromTime.entrySet()) {
            final Guild guild = jda.getGuildById(entry.getKey());
            if (Objects.isNull(guild)) {
                removeFromGuild.add(entry.getKey());
                continue;
            }
            final Integer maxInactivity = handler.getPossibleRemoteProperty(RemoteProperty.R_INACTIVITY_EMPTY_TIMEOUT,
                guild, Integer.class);
            if (maxInactivity < 1) continue;
            if (entry.getValue().getEpochSecond() > (Instant.now().getEpochSecond() - maxInactivity)) {
                continue;
            }
            final MusicManager musicManager = playerManager.getMusicManager(guild);
            musicManager.getActions().clearAndDestroy(true);
            guild.getAudioManager().closeAudioConnection();

            log.info("G: {} <> Leave voice channel. Cause: not found any active user", guild.getName());
            removeFromGuild.add(entry.getKey());
        }
        removeFromGuild.forEach(aloneFromTime::remove);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean isAloneOnChannel(Guild guild) {
        if (Objects.isNull(guild.getAudioManager().getConnectedChannel())) return false;

        return guild.getAudioManager().getConnectedChannel().getMembers().stream()
            .noneMatch(m -> !Objects.isNull(m.getVoiceState()) && !m.getVoiceState().isDeafened() && !m.getUser().isBot());
    }
}
