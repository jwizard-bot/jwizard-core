/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AloneOnChannelListener.java
 * Last modified: 26/03/2023, 13:27
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

package pl.miloszgilga.audioplayer;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class AloneOnChannelListener {

    private final BotConfiguration config;
    private final PlayerManager playerManager;

    private JDA jda;
    private int maxInactivitySeconds;
    private final Map<Long, Instant> aloneFromTime = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    AloneOnChannelListener(BotConfiguration config, PlayerManager playerManager) {
        this.config = config;
        this.playerManager = playerManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initialize(JDA jda) {
        this.jda = jda;
        maxInactivitySeconds = config.getProperty(BotProperty.J_INACTIVITY_EMPTY_TIMEOUT, Integer.class);
        if (maxInactivitySeconds < 1) return;
        config.getThreadPool().scheduleWithFixedDelay(this::coccurentAloneCheck, 0, 5, TimeUnit.SECONDS);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onEveryVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (maxInactivitySeconds < 1) return;

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
            if (entry.getValue().getEpochSecond() > (Instant.now().getEpochSecond() - maxInactivitySeconds)) {
                continue;
            }
            final Guild guild = jda.getGuildById(entry.getKey());
            if (Objects.isNull(guild)) {
                removeFromGuild.add(entry.getKey());
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
