/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioQueueShufleCommandExecutor.java
 * Last modified: 15/07/2022, 00:35
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
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Queue;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.messages.EmbedMessageColor;
import static pl.miloszgilga.Command.MUSIC_SHUFFLE;
import pl.miloszgilga.exceptions.EmptyAudioQueueException;
import static pl.miloszgilga.executors.AudioShowAllQueueCommandExecutor.showQueueElementsInEmbedMessage;


public class AudioQueueShuffleCommandExecutor extends Command {

    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public AudioQueueShuffleCommandExecutor() {
        name = MUSIC_SHUFFLE.getCommandName();
        help = MUSIC_SHUFFLE.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]shuffle>")
    protected void execute(CommandEvent event) {
        try {
            final Queue<AudioTrack> queue = playerManager.getMusicManager(event.getGuild()).getScheduler().getQueue();
            if (queue.isEmpty()) {
                throw new EmptyAudioQueueException(event);
            }
            playerManager.getMusicManager(event.getGuild()).getScheduler().queueShuffle();
            final var embedMessage = new EmbedMessage("INFO", "Piosenki w kolejce zostały przemieszane.",
                    EmbedMessageColor.GREEN);
            event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
            showQueueElementsInEmbedMessage(event, queue);
        } catch (EmptyAudioQueueException ex) {
            System.out.println(ex.getMessage());
        }
    }
}