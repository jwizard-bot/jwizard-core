/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: NextTrackCommandExecutor.java
 * Last modified: 15/07/2022, 08:25
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

package pl.miloszgilga.franekbotapp.executors.audioplayer;

import jdk.jfr.Description;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.Queue;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.audioplayer.EventWrapper;
import pl.miloszgilga.franekbotapp.audioplayer.PlayerManager;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.audioplayer.QueueTrackExtendedInfo;
import pl.miloszgilga.franekbotapp.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.franekbotapp.exceptions.UnableAccessToInvokeCommandException;

import static pl.miloszgilga.franekbotapp.BotCommand.MUSIC_SKIP;
import static pl.miloszgilga.franekbotapp.executors.audioplayer.PauseTrackCommandExecutor.checkIfActionEventInvokeBySender;
import static pl.miloszgilga.franekbotapp.executors.audioplayer.VoteSkipTrackCommandExecutor.findVoiceChannelWithBotAndUser;


public final class SkipTrackCommandExecutor extends Command {

    private final LoggerFactory logger = new LoggerFactory(SkipTrackCommandExecutor.class);
    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public SkipTrackCommandExecutor() {
        name = MUSIC_SKIP.getCommandName();
        help = MUSIC_SKIP.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]skip>")
    protected void execute(CommandEvent event) {
        try {
            final var eventWrapper = new EventWrapper(event);
            final Queue<QueueTrackExtendedInfo> queue = playerManager.getMusicManager(eventWrapper).getScheduler().getQueue();
            if (playerManager.getMusicManager(eventWrapper).getAudioPlayer().getPlayingTrack() == null) {
                throw new EmptyAudioQueueException(event);
            }

            findVoiceChannelWithBotAndUser(event);
            checkIfActionEventInvokeBySender(event);

            final var embedMessage = new EmbedMessage("", "Pominięto piosenkę.",
                    EmbedMessageColor.GREEN);
            event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
            playerManager.getMusicManager(eventWrapper).getScheduler().setRepeating(false);

            final AudioTrackInfo audioTrackInfo = playerManager.getMusicManager(eventWrapper).getAudioPlayer()
                    .getPlayingTrack().getInfo();
            logger.info(event.getGuild(), "Odtwarzanie piosenki '%s' zostało pominięte przez dodającego '%s'",
                    audioTrackInfo.title, event.getAuthor().getAsTag());
            if (queue.isEmpty()) {
                playerManager.getMusicManager(eventWrapper).getAudioPlayer().stopTrack();
            } else {
                playerManager.getMusicManager(eventWrapper).getScheduler().nextTrack();
            }
        } catch (EmptyAudioQueueException | UnableAccessToInvokeCommandException ex) {
            logger.warn(event.getGuild(), ex.getMessage());
        }
    }
}
