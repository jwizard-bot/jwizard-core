/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioSkippedCommandExecutor.java
 * Last modified: 12/07/2022, 00:19
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

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import jdk.jfr.Description;
import org.jetbrains.annotations.NotNull;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.messages.EmbedMessageColor;
import pl.miloszgilga.interceptors.SendMessageInterceptor;
import static pl.miloszgilga.AvailableCommands.MUSIC_SKIP;
import pl.miloszgilga.exceptions.UserOnVoiceChannelNotFoundException;
import pl.miloszgilga.exceptions.AttemptToRevoteSkippingSongException;


public class AudioSkippedCommandExecutor extends ListenerAdapter {

    private final SendMessageInterceptor interceptor = SendMessageInterceptor.getSingletonInstance();
    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    private final Queue<String> usersAlreadyVoted = new PriorityQueue<>();

    @Override
    @Description("command: <[prefix]skip>")
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final List<String> allArgs = interceptor.validateRequestWithCommandType(event, MUSIC_SKIP);
        if (!allArgs.isEmpty()) {
            try {
                String messageAuthorId = event.getAuthor().getId();
                VoiceChannel findVoiceChannelWithBot = findVoiceChannelWithBotAndUser(event);

                int allChannelMembers = findVoiceChannelWithBot.getMembers().size() - 1;
                int requireVotesToSkipSong = allChannelMembers == 1 ? 1 : allChannelMembers / 2;

                if (!usersAlreadyVoted.contains(messageAuthorId)) {
                    usersAlreadyVoted.add(messageAuthorId);
                    var votingProgress = new EmbedMessage("POMINIĘCIE PIOSENKI", String.format(
                            "Status głosowania: **%s**/**%s**, (wymagane głosów: **%s**)",
                            usersAlreadyVoted.size(), allChannelMembers, requireVotesToSkipSong),
                            EmbedMessageColor.GREEN
                    );
                    event.getTextChannel().sendMessageEmbeds(votingProgress.buildMessage()).queue();
                    if (usersAlreadyVoted.size() == allChannelMembers) {
                        usersAlreadyVoted.clear();
                        playerManager.getMusicManager(event.getGuild()).scheduler.nextTrack();
                        var votingEnded = new EmbedMessage("PIOSENKA POMINIĘTA", "", EmbedMessageColor.GREEN);
                        event.getTextChannel().sendMessageEmbeds(votingEnded.buildMessage()).queue();
                    }
                } else {
                    throw new AttemptToRevoteSkippingSongException(event);
                }
            } catch (UserOnVoiceChannelNotFoundException | AttemptToRevoteSkippingSongException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private VoiceChannel findVoiceChannelWithBotAndUser(MessageReceivedEvent event) {
        String guildId = event.getGuild().getId();
        return Objects.requireNonNull(event.getJDA().getGuildById(guildId))
                .getVoiceChannels().stream()
                .filter(channel -> {
                    Member senderUserMember = event.getGuild().getMember(event.getAuthor());
                    Member botMember = event.getGuild().getMember(event.getJDA().getSelfUser());
                    return channel.getMembers().contains(senderUserMember) && channel.getMembers().contains(botMember);
                })
                .findFirst().orElseThrow(() -> {
                    throw new UserOnVoiceChannelNotFoundException(event, "Aby móc uczestniczyć w głosowaniu na " +
                            "pominięcie piosenki, musisz przebywać na kanale głosowym wraz z botem.");
                });
    }
}