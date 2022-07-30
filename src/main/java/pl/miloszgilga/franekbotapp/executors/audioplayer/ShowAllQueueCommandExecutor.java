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

package pl.miloszgilga.franekbotapp.executors.audioplayer;

import jdk.jfr.Description;
import net.dv8tion.jda.api.EmbedBuilder;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.audioplayer.EventWrapper;
import pl.miloszgilga.franekbotapp.audioplayer.PlayerManager;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.audioplayer.QueueTrackExtendedInfo;
import pl.miloszgilga.franekbotapp.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.franekbotapp.exceptions.IllegalCommandArgumentsException;

import static pl.miloszgilga.franekbotapp.BotCommand.MUSIC_QUEUE;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


public final class ShowAllQueueCommandExecutor extends Command {

    private final LoggerFactory logger = new LoggerFactory(ShowAllQueueCommandExecutor.class);
    private static final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public ShowAllQueueCommandExecutor() {
        name = MUSIC_QUEUE.getCommandName();
        help = MUSIC_QUEUE.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]queue>")
    protected void execute(CommandEvent event) {
        try {
            final Queue<QueueTrackExtendedInfo> queue = playerManager.getMusicManager(new EventWrapper(event))
                    .getScheduler().getQueue();
            if (queue.isEmpty()) {
                throw new EmptyAudioQueueException(event);
            }
            showQueueElementsInEmbedMessage(event, queue);
        } catch (EmptyAudioQueueException | IllegalCommandArgumentsException ex) {
            logger.warn(ex.getMessage(), event.getGuild());
        }
    }

    static void showQueueElementsInEmbedMessage(CommandEvent event, Queue<QueueTrackExtendedInfo> queue) {
        final List<AudioTrack> flattedQueue = queue.stream()
                .map(QueueTrackExtendedInfo::getAudioTrack).collect(Collectors.toList());
        final long maxQueueMilis = flattedQueue.stream().mapToLong(AudioTrack::getDuration).sum();

        int countOfPages = (int)Math.ceil((double)flattedQueue.size() / config.getQueuePaginationMaxElmsOnPage());
        int pagePosition = 1;

        try {
            if (!event.getArgs().equals("")) {
                pagePosition = Integer.parseInt(event.getArgs());
            }
            if (pagePosition > countOfPages) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            throw new IllegalCommandArgumentsException(event, MUSIC_QUEUE, String.format(
                    "`%s%s [nr strony (opcjonalny)]`", config.getPrefix(), MUSIC_QUEUE.getCommandName()));
        }

        final int indexFrom = (pagePosition - 1) * config.getQueuePaginationMaxElmsOnPage();
        final int indexTo = Math.min(indexFrom + config.getQueuePaginationMaxElmsOnPage(), flattedQueue.size());
        final List<AudioTrack> singlePageQueue = flattedQueue.subList(indexFrom, indexTo);

        final var embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(EmbedMessageColor.GREEN.getColor()));
        embedBuilder.setTitle(String.format("KOLEJKA (Strona %s z %s)", pagePosition, countOfPages));
        embedBuilder.setDescription(String.format("Ilość piosenek w kolejce: **%s**, Czas trwania: **%s**",
                queue.size(), convertMilisToDateFormat(maxQueueMilis)));
        for (int i = 0; i < singlePageQueue.size(); i++) {
            AudioTrack track = singlePageQueue.get(i);
            embedBuilder.addField(
                    String.format("%s: %s", track.getInfo().author, track.getInfo().title),
                    String.format("Pozycja: %s, Czas trwania: %s",
                            indexFrom + i + 1, convertMilisToDateFormat(track.getDuration())),
                    false);
        }
        embedBuilder.setFooter("Aby przejść na kolejną stronę użyj komendy z argumentami.");
        event.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static String convertMilisToDateFormat(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}