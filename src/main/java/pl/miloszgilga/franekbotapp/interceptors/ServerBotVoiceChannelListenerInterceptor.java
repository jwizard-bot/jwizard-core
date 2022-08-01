/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ServerBotDeafenInterceptor.java
 * Last modified: 15/07/2022, 17:32
 * Project name: franek-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.franekbotapp.interceptors;

import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.audioplayer.EventWrapper;
import pl.miloszgilga.franekbotapp.audioplayer.MusicManager;
import pl.miloszgilga.franekbotapp.audioplayer.PlayerManager;
import pl.miloszgilga.franekbotapp.executorhandlers.ExecutorTimer;

import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


public final class ServerBotVoiceChannelListenerInterceptor extends ListenerAdapter {

    private final byte ELAPSE_TIME = config.getMaxInactivityTimeAfterPauseTrackMinutes();
    private final LoggerFactory logger = new LoggerFactory(ServerBotVoiceChannelListenerInterceptor.class);
    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    private ExecutorTimer executorTimer;

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        Optional<Member> botMember = event.getChannelJoined().getMembers().stream()
                .filter(member -> member.getUser().isBot()).findFirst();
        botMember.ifPresent(member -> member.deafen(true).complete());
        if (executorTimer != null) executorTimer.interrupt();
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        final Member botMember = event.getGuild().getMember(event.getJDA().getSelfUser());
        final EventWrapper eventWrapper = new EventWrapper(event);
        final List<Member> allChannelMembersWithoutBot = event.getChannelLeft().getMembers().stream()
                .filter(member -> !member.getUser().isBot())
                .collect(Collectors.toList());
        if (allChannelMembersWithoutBot.isEmpty() && event.getChannelLeft().getMembers().contains(botMember)) {
            executorTimer = new ExecutorTimer(ELAPSE_TIME, () -> {
                final MusicManager musicManager = playerManager.getMusicManager(eventWrapper);
                event.getJDA().getDirectAudioController().disconnect(event.getGuild());
                musicManager.getAudioPlayer().stopTrack();
                musicManager.getScheduler().getQueue().clear();
                logger.warn(String.format(
                        "Opuszczenie kanału głosowego '%s' w wyniku braku aktywnych użytkowników przez '%s' minut",
                        event.getChannelLeft().getName(), ELAPSE_TIME
                ), null);
            });
            executorTimer.execute();
        }
    }
}