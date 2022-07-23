/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: VoteCommandExecutingHandler.java
 * Last modified: 15/07/2022, 23:46
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

package pl.miloszgilga.franekbotapp.executors.executorhandlers;

import lombok.Getter;
import net.dv8tion.jda.api.entities.VoiceChannel;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.*;

import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.audioplayer.PlayerManager;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.exceptions.AttemptToRevoteSkippingSongException;

import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


@Getter
public class VoteCommandExecutingHandler {

    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    private final CommandEvent event;
    private final String votePassEmbedTitle;
    private final String voteTakesEmbedTitle;
    private final String voteFailureEmbedTitle;
    private final int allChannelMembers;
    private final int requireVotesToPass;
    private boolean isSuccessVoted = false;
    private final VoiceChannel voiceChannel;
    private final Queue<String> usersAlreadyVoted = new PriorityQueue<>();
    private final List<String> usersVoted = new ArrayList<>();

    public VoteCommandExecutingHandler(CommandEvent event, VoiceChannel voiceChannel, String votePassEmbedTitle,
                                       String voteTakesEmbedTitle, String voteFailureEmbedTitle) {
        this.event = event;
        this.voiceChannel = voiceChannel;
        this.votePassEmbedTitle = votePassEmbedTitle.toUpperCase(Locale.ROOT);
        this.voteTakesEmbedTitle = voteTakesEmbedTitle.toUpperCase(Locale.ROOT);
        this.voteFailureEmbedTitle = voteFailureEmbedTitle.toUpperCase(Locale.ROOT);
        allChannelMembers = voiceChannel.getMembers().size() - 1;
        requireVotesToPass = (allChannelMembers / 2) + 1;
    }

    public boolean voteCommandExecutor() {
        Thread countingUntilVoteElapsed = countingUntilVoteElapsed();
        if (!countingUntilVoteElapsed.isInterrupted() && config.getMaxVotingElapseTimeMinutes() >= 0) {
            countingUntilVoteElapsed.interrupt();
        }
        if (usersAlreadyVoted.isEmpty()) usersVoted.clear();

        if (!usersAlreadyVoted.contains(event.getAuthor().getId())) {
            usersAlreadyVoted.add(event.getAuthor().getId());
            usersVoted.add(event.getAuthor().getAsTag());
            if (config.getMaxVotingElapseTimeMinutes() >= 0) countingUntilVoteElapsed.start();
            final var takesEmbed = new EmbedMessage(voteTakesEmbedTitle, String.format(
                    "Status głosowania: **%s**/**%s**, (wymagane głosów: **%s**)",
                    usersAlreadyVoted.size(), allChannelMembers, requireVotesToPass), EmbedMessageColor.GREEN
            );
            event.getTextChannel().sendMessageEmbeds(takesEmbed.buildMessage()).queue();
            if (usersAlreadyVoted.size() == requireVotesToPass) {
                usersAlreadyVoted.clear();
                isSuccessVoted = true;
                final var passEmbed = new EmbedMessage(votePassEmbedTitle, "", EmbedMessageColor.GREEN);
                event.getTextChannel().sendMessageEmbeds(passEmbed.buildMessage()).queue();
                return true;
            }
        } else {
            throw new AttemptToRevoteSkippingSongException(event);
        }
        return false;
    }

    private Thread countingUntilVoteElapsed() {
        return new Thread(() -> {
            try {
                Thread.sleep(1000 * config.getMaxVotingElapseTimeMinutes());
                if (!isSuccessVoted) {
                    usersAlreadyVoted.clear();
                    final var leavingMessage = new EmbedMessage("GŁOSOWANIE ZAKOŃCZONE NIEPOWODZENIEM", "",
                            EmbedMessageColor.RED);
                    event.getTextChannel().sendMessageEmbeds(leavingMessage.buildMessage()).queue();
                }
            } catch (InterruptedException ignored) { }
        });
    }

    public String allVotedUsers() {
        return "[" + String.join(",", usersVoted) + "]";
    }
}