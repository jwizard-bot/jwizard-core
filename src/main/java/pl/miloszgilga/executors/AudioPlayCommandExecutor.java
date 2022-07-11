/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioPlayCommandExecutor.java
 * Last modified: 11/07/2022, 23:35
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

package pl.miloszgilga.executors;

import jdk.jfr.Description;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.exceptions.UserOnVoiceChannelNotFoundException;
import pl.miloszgilga.interceptors.SendMessageInterceptor;
import pl.miloszgilga.exceptions.IllegalCommandArgumentsException;
import static pl.miloszgilga.FranekBot.DEF_PREFIX;
import static pl.miloszgilga.AvailableCommands.MUSIC_PLAY;
import static pl.miloszgilga.Utils.isUrl;


public class AudioPlayCommandExecutor extends ListenerAdapter {

    private final SendMessageInterceptor interceptor = SendMessageInterceptor.getSingletonInstance();
    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    @Override
    @Description("command: <[prefix]play [music link or description]>")
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final List<String> allArgs = interceptor.validateRequestWithCommandType(event, MUSIC_PLAY);
        if (!allArgs.isEmpty()) {
            try {
                if (allArgs.size() < 2) {
                    throw new IllegalCommandArgumentsException(event, String.format(
                            "`%s [link lub nazwa piosenki]`", DEF_PREFIX + MUSIC_PLAY.getCommandName()));
                }
                if (!Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).inAudioChannel()) {
                    throw new UserOnVoiceChannelNotFoundException(event, "Aby możliwe było odtworzenie piosenki, " +
                            "musisz znajdować się na kanale głosowym.");
                }

                final AudioManager audioManager = event.getGuild().getAudioManager();
                final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                audioManager.openAudioConnection(memberChannel);

                allArgs.remove(0);
                String withoutPrefix  = String.join(" ", allArgs);
                if (!isUrl(withoutPrefix) && allArgs.size() > 2) {
                    withoutPrefix = "ytsearch: " + withoutPrefix + " audio";
                } else {
                    withoutPrefix = withoutPrefix.replaceAll(" ", "");
                }
                playerManager.loadAndPlay(event.getTextChannel(), withoutPrefix);

            } catch (UserOnVoiceChannelNotFoundException | IllegalCommandArgumentsException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}