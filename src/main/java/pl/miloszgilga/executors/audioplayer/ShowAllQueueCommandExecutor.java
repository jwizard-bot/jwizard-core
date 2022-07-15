/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioShowAllQueueCommandExecutor.java
 * Last modified: 15/07/2022, 02:30
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

package pl.miloszgilga.executors.audioplayer;

import jdk.jfr.Description;
import net.dv8tion.jda.api.EmbedBuilder;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.awt.*;
import java.util.Date;
import java.util.Queue;
import java.text.SimpleDateFormat;

import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.messages.EmbedMessageColor;
import static pl.miloszgilga.Command.MUSIC_QUEUE;
import pl.miloszgilga.exceptions.EmptyAudioQueueException;


public class ShowAllQueueCommandExecutor extends Command {

    private static final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public ShowAllQueueCommandExecutor() {
        name = MUSIC_QUEUE.getCommandName();
        help = MUSIC_QUEUE.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]queue>")
    protected void execute(CommandEvent event) {
        try {
            final Queue<AudioTrack> queue = playerManager.getMusicManager(event.getGuild()).getScheduler().getQueue();
            if (queue.isEmpty()) {
                throw new EmptyAudioQueueException(event);
            }
            showQueueElementsInEmbedMessage(event, queue);
        } catch (EmptyAudioQueueException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void showQueueElementsInEmbedMessage(CommandEvent event, Queue<AudioTrack> queue) {
        int position = 0;
        final long maxQueueMilis = queue.stream().mapToLong(AudioTrack::getDuration).sum();

        final var embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("KOLEJKA");
        embedBuilder.setDescription(String.format("Ilość piosenek w kolejce: **%s**, Czas trwania: **%s**",
                queue.size(), convertMilisToDateFormat(maxQueueMilis)));
        embedBuilder.setColor(Color.decode(EmbedMessageColor.GREEN.getColor()));
        for (AudioTrack track : queue) {
            embedBuilder.addField(
                    String.format("%s - %s", track.getInfo().author, track.getInfo().title),
                    String.format("Pozycja: %s, Czas trwania: %s",
                            ++position, convertMilisToDateFormat(track.getDuration())),
                    false);
        }
        event.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private static String convertMilisToDateFormat(long milis) {
        return (new SimpleDateFormat("mm:ss")).format(new Date(milis));
    }
}