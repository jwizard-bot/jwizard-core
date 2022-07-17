/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioLoaderResult.java
 * Last modified: 15/07/2022, 03:12
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

package pl.miloszgilga.franekbotapp.audioplayer;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

import java.awt.*;
import java.util.List;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;

import static pl.miloszgilga.franekbotapp.executors.audioplayer.ShowAllQueueCommandExecutor.convertMilisToDateFormat;


@AllArgsConstructor
class AudioLoaderResult implements AudioLoadResultHandler {

    private final LoggerFactory logger = new LoggerFactory(AudioLoaderResult.class);

    private final CommandEvent event;
    private final boolean ifValidUri;
    private final MusicManager musicManager;

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        final Member senderUser = event.getGuild().getMember(event.getAuthor());
        audioTrack.setUserData(senderUser);
        musicManager.getScheduler().queue(new QueueTrackExtendedInfo(senderUser, audioTrack));
        if (!musicManager.getScheduler().getQueue().isEmpty()) {
            onSingleTrackLoadedSendEmbedMessage(audioTrack);
            logger.info(String.format("Użytkownik '%s' dodał nową piosenkę do kolejki '%s'",
                    event.getAuthor().getAsTag(), audioTrack.getInfo().title), event.getGuild());
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        final List<AudioTrack> trackList = audioPlaylist.getTracks();
        final Member senderUser = event.getGuild().getMember(event.getAuthor());
        if (trackList.isEmpty()) {
            return;
        }
        if (ifValidUri) {
            onPlaylistLoadedSendEmbedMessage(trackList, audioPlaylist);
            for(int i = 0; i < audioPlaylist.getTracks().size(); i++) {
                audioPlaylist.getTracks().get(i).setUserData(senderUser);
                musicManager.getScheduler().queue(new QueueTrackExtendedInfo(senderUser, trackList.get(i)));
            }
            logger.info(String.format("Użytkownik '%s' dodał nową playlistę do kolejki składającą się z '%s' piosenek",
                    event.getAuthor().getAsTag(), audioPlaylist.getTracks().size()), event.getGuild());
        } else {
            audioPlaylist.getTracks().get(0).setUserData(senderUser);
            musicManager.getScheduler().queue(new QueueTrackExtendedInfo(senderUser, audioPlaylist.getTracks().get(0)));
            if (!musicManager.getScheduler().getQueue().isEmpty()) {
                onSingleTrackLoadedSendEmbedMessage(audioPlaylist.getTracks().get(0));
                logger.info(String.format("Użytkownik '%s' dodał nową piosenkę do kolejki '%s'",
                        event.getAuthor().getAsTag(), audioPlaylist.getTracks().get(0).getInfo().title), event.getGuild());
            }
        }
    }

    @Override
    public void noMatches() {
        final var embedMessage = new EmbedMessage("ERROR!", "Nie znaleziono piosenki na podstawie podanych parametrów.",
                EmbedMessageColor.RED
        );
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
        logger.error(String.format("Nie znaleziono piosenki z wprowadzonej przez '%s' podaną nazwą '%s'",
                event.getAuthor().getAsTag(), event.getArgs()), event.getGuild());
    }

    @Override
    public void loadFailed(FriendlyException e) {
        final var embedMessage = new EmbedMessage("ERROR!",
                "Nastąpił nieznany błąd przy próbie odtworzenia piosenki/playlisty. Spróbuj ponownie.",
                EmbedMessageColor.RED
        );
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
        logger.error(String.format("Wystąpił nieznany błąd podczas dodawania piosenki/playlisty przez '%s'",
                event.getAuthor().getAsTag()), event.getGuild());
    }

    private void onSingleTrackLoadedSendEmbedMessage(AudioTrack track) {
        final var embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Dodano nową piosenkę do kolejki.");
        embedBuilder.setColor(Color.decode(EmbedMessageColor.GREEN.getColor()));
        embedBuilder.addField("Nazwa:", String.format("[%s](%s)", track.getInfo().title, track.getInfo().uri), true);
        embedBuilder.addBlankField(true);
        embedBuilder.addField("Czas trwania:", convertMilisToDateFormat(track.getDuration()), true);
        embedBuilder.addField("Pozycja w kolejce:", musicManager.getScheduler().queueTrackPositionBaseId(), true);
        embedBuilder.addBlankField(true);
        embedBuilder.addField("Dodana przez:", event.getAuthor().getName(), true);
        event.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void onPlaylistLoadedSendEmbedMessage(List<AudioTrack> trackList, AudioPlaylist playlist) {
        final long maxQueueMilis = trackList.stream().mapToLong(AudioTrack::getDuration).sum();
        final var embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Dodano nową playlistę do kolejki.");
        embedBuilder.setColor(Color.decode(EmbedMessageColor.GREEN.getColor()));
        embedBuilder.addField("Ilość piosenek:", Integer.toString(playlist.getTracks().size()), true);
        embedBuilder.addBlankField(true);
        embedBuilder.addField("Całkowity czas trwania:", convertMilisToDateFormat(maxQueueMilis), true);
        embedBuilder.addField("Dodana przez:", event.getAuthor().getName(), true);
        event.getTextChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}