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

package pl.miloszgilga.audioplayer;

import lombok.Getter;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

import java.util.*;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.messages.EmbedMessageColor;

import static pl.miloszgilga.FranekBot.config;


@Getter
public class TrackScheduler extends AudioEventAdapter {

    private final CommandEvent event;
    private final AudioPlayer audioPlayer;
    private final Queue<QueueTrackExtendedInfo> queue = new LinkedList<>();

    private boolean repeating = false;
    private boolean alreadyDisplayed = false;
    private Thread counttingToLeaveTheChannel;

    public TrackScheduler(AudioPlayer audioPlayer, CommandEvent event) {
        this.audioPlayer = audioPlayer;
        this.event = event;
    }

    public void queue(QueueTrackExtendedInfo queueTrackExtendedInfo) {
        if (!audioPlayer.startTrack(queueTrackExtendedInfo.getAudioTrack(), true)) {
            queue.offer(queueTrackExtendedInfo);
        }
    }

    public void nextTrack() {
        audioPlayer.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
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