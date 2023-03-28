/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotStatusCommandListener.java
 * Last modified: 24/03/2023, 14:53
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

package pl.miloszgilga.listener;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

import java.util.List;
import java.util.ArrayList;

import pl.miloszgilga.embed.EmbedColor;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.AloneOnChannelListener;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableListenerLazyService
public class BotStatusCommandListener extends AbstractListenerAdapter {

    private boolean shuttingDown = false;

    private final AloneOnChannelListener aloneOnChannelListener;
    private final PlayerManager playerManager;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BotStatusCommandListener(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, AloneOnChannelListener aloneOnChannelListener,
        PlayerManager playerManager
    ) {
        super(config, embedBuilder);
        this.aloneOnChannelListener = aloneOnChannelListener;
        this.playerManager = playerManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onReady(ReadyEvent event) {
        final String defaultDjRoleName = config.getProperty(BotProperty.J_DJ_ROLE_NAME);
        final List<String> addedDjRolesIntoGulds = new ArrayList<>();
        for (final Guild guild : event.getJDA().getGuilds()) {
            final boolean roleAlreadyExist = guild.getRoles().stream()
                .anyMatch(r -> r.getName().equals(defaultDjRoleName));
            if (roleAlreadyExist) continue;
            addedDjRolesIntoGulds.add(guild.getName());
            guild.createRole()
                .setName(defaultDjRoleName)
                .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
                .submit();
        }
        log.info("DJ role '{}' for guilds '{}' was successfully created", defaultDjRoleName, addedDjRolesIntoGulds);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!event.getMember().getUser().isBot()) return;
        event.getGuild().getAudioManager().setSelfDeafened(true);
        event.getGuild().getSelfMember().deafen(true).complete();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        aloneOnChannelListener.onEveryVoiceUpdate(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onShutdown(ShutdownEvent event) {
        if (shuttingDown) return;
        shuttingDown = true;
        super.config.getThreadPool().shutdownNow();
        if (event.getJDA().getStatus() == JDA.Status.SHUTTING_DOWN) return;

        for (final Guild guild : event.getJDA().getGuilds()) {
            guild.getAudioManager().closeAudioConnection();
            playerManager.getMusicManager(guild).getTrackScheduler().clearAndDestroy(false);
        }
        event.getJDA().shutdown();
        System.exit(0);
    }
}
