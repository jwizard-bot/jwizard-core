/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: TrackScheduler.java
 * Last modified: 12/07/2022, 00:17
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

import lombok.Getter;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

import java.util.*;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;

import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


@Getter
public class TrackScheduler extends AudioEventAdapter {

    private final LoggerFactory logger = new LoggerFactory(TrackScheduler.class);

    private final EventWrapper event;
    private final AudioPlayer audioPlayer;
    private final Queue<QueueTrackExtendedInfo> queue = new LinkedList<>();

    private boolean repeating = false;
    private boolean alreadyDisplayed = false;
    private Thread countingToLeaveTheChannel;

    TrackScheduler(AudioPlayer audioPlayer, EventWrapper event) {
        this.audioPlayer = audioPlayer;
        this.event = event;
    }

    void queue(QueueTrackExtendedInfo queueTrackExtendedInfo) {
        if (audioPlayer.startTrack(queueTrackExtendedInfo.getAudioTrack(), true)) return;
        queue.offer(queueTrackExtendedInfo);
    }

    public void nextTrack() {
        final QueueTrackExtendedInfo queueTrackExtendedInfo = queue.poll();
        if (queueTrackExtendedInfo == null) return;
        audioPlayer.startTrack(queueTrackExtendedInfo.getAudioTrack(), false);
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        final AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
        if (!audioPlayer.isPaused()) return;

        final var embedMessage = new EmbedMessage("", String.format(
                "Zatrzymałem odtwarzanie piosenki: **%s**.", info.title), EmbedMessageColor.GREEN);
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();

        logger.info(String.format("Odtwarzanie piosenki '%s' zostało wstrzymane przez '%s'",
                info.title, event.getUser().getAsTag()), event.getGuild());
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        final AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
        if (audioPlayer.isPaused()) return;

        final var embedMessage = new EmbedMessage("", String.format(
                "Ponawiam odtwarzanie piosenki: **%s**.", info.title), EmbedMessageColor.GREEN);
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();

        logger.info(String.format("Odtwarzanie piosenki '%s' zostało ponowione przez '%s'",
                info.title, event.getUser().getAsTag()), event.getGuild());
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (countingToLeaveTheChannel != null) countingToLeaveTheChannel.interrupt();
        if (alreadyDisplayed) return;

        final AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
        final var embedMessage = new EmbedMessage("", String.format("Rozpoczynam odtwarzanie piosenki: **%s**.",
                info.title), EmbedMessageColor.GREEN);
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();

        if (repeating) alreadyDisplayed = true;

        logger.info(String.format("Automatyczne odtwarzanie piosenki '%s' dodanej przez '%s'",
                info.title, event.getUser().getAsTag()), event.getGuild());
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (audioPlayer.getPlayingTrack() == null && queue.isEmpty() && !repeating) {
            final var embedMessage = new EmbedMessage("", "Koniec kolejki odtwarzania.", EmbedMessageColor.RED);
            event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
            if (config.getMaxInactivityTimeMinutes() < 0) return;
            countingToLeaveTheChannel = new Thread(() -> {
                try {
                    Thread.sleep(1000 * 60 * config.getMaxInactivityTimeMinutes());
                    final var leavingMessage = new EmbedMessage("", String.format(
                            "W związku z brakiem aktywności przez %s minut opuszczam kanał głosowy. Z fartem.",
                            config.getMaxInactivityTimeMinutes()), EmbedMessageColor.RED
                    );
                    event.getTextChannel().sendMessageEmbeds(leavingMessage.buildMessage()).queue();
                    event.getJda().getDirectAudioController().disconnect(event.getGuild());
                    logger.warn(String.format(
                            "Automatyczne opuszczenie kanału głosowego przez bota po %s minutach nieaktywności",
                            config.getMaxInactivityTimeMinutes()), event.getGuild());
                } catch (InterruptedException ignored) { }
            });
            countingToLeaveTheChannel.start();
            return;
        }
        if (endReason.mayStartNext) {
            if (repeating) {
                audioPlayer.startTrack(track.makeClone(), false);
            } else {
                nextTrack();
            }
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        final var embedMessage = new EmbedMessage("ERROR!",
                "Napotkałem nieznany błąd przy próbie odtworzenia piosenki/playlisty. Spróbuj ponownie.",
                EmbedMessageColor.RED
        );
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();

        logger.error(String.format("Wystąpił nieznany błąd podczas dodawania piosenki/playlisty przez '%s'",
                event.getUser().getAsTag()), event.getGuild());
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
        if (!repeating) {
            alreadyDisplayed = false;
        }
    }

    public void queueShuffle() {
        Collections.shuffle((List<?>) queue);
    }

    public String queueTrackPositionBaseId() {
        if (queue.size() == 1) {
            return "Następna";
        }
        return Integer.toString(queue.size());
    }
}