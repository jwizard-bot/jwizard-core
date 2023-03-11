/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: TrackScheduler.java
 * Last modified: 04/03/2023, 23:05
 * Project name: jwizard-discord-bot
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

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.MessageEmbed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import java.util.Map;
import java.util.Queue;
import java.util.Objects;
import java.util.LinkedList;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
class TrackScheduler extends AudioEventAdapter {

    private final BotConfiguration config;
    private final EmbedMessageBuilder builder;
    private final AudioPlayer audioPlayer;
    private final EventWrapper deliveryEvent;

    private final Queue<AudioQueueExtendedInfo> trackQueue = new LinkedList<>();

    private AudioTrack pausedTrack;
    private int countOfRepeats = 0;
    private boolean repeating = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    TrackScheduler(
        BotConfiguration config, EmbedMessageBuilder builder, AudioPlayer audioPlayer, EventWrapper deliveryEvent
    ) {
        this.config = config;
        this.builder = builder;
        this.audioPlayer = audioPlayer;
        this.deliveryEvent = deliveryEvent;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPlayerPause(AudioPlayer player) {
        if (Objects.isNull(audioPlayer.getPlayingTrack())) return;

        final AudioTrackInfo trackInfo = audioPlayer.getPlayingTrack().getInfo();
        pausedTrack = audioPlayer.getPlayingTrack();

        final String rawMessage = config.getLocaleText(LocaleSet.PAUSE_TRACK_MESS, Map.of(
            "track", String.format("[%s](%s)", trackInfo.title, trackInfo.uri),
            "invoker", deliveryEvent.authorTag(),
            "resumeCmd", BotCommand.RESUME_TRACK.parseWithPrefix(config)
        ));
        final MessageEmbed messageEmbed = builder.createMessage(deliveryEvent, rawMessage);
        deliveryEvent.textChannel().sendMessageEmbeds(messageEmbed).queue();

        log.info("G: {}, A: {} <> Audio track: '{}' was paused", deliveryEvent.guildName(),
            deliveryEvent.authorTag(), trackInfo.title);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPlayerResume(AudioPlayer player) {
        if (Objects.isNull(pausedTrack)) return;

        final AudioTrackInfo trackInfo = audioPlayer.getPlayingTrack().getInfo();
        pausedTrack = null;

        final String rawMessage = config.getLocaleText(LocaleSet.RESUME_TRACK_MESS, Map.of(
            "track", String.format("[%s](%s)", trackInfo.title, trackInfo.uri),
            "invoker", deliveryEvent.authorTag(),
            "pauseCmd", BotCommand.PAUSE_TRACK.parseWithPrefix(config)
        ));
        final MessageEmbed messageEmbed = builder.createMessage(deliveryEvent, rawMessage);
        deliveryEvent.textChannel().sendMessageEmbeds(messageEmbed).queue();

        log.info("G: {}, A: {} <> Paused audio track: '{}' was resumed", deliveryEvent.guildName(),
            deliveryEvent.authorTag(), trackInfo.title);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException ex) {
        final MessageEmbed messageEmbed = builder.createErrorMessage(deliveryEvent,
            config.getLocaleText(LocaleSet.ISSUE_WHILE_PLAYING_TRACK_MESS), BugTracker.ISSUE_WHILE_PLAYING_TRACK);
        deliveryEvent.textChannel().sendMessageEmbeds(messageEmbed).queue();

        log.error("G: {}, A: {} <> Unexpected issue while playing track: '{}'. Cause: {}", deliveryEvent.guildName(),
            deliveryEvent.authorTag(), track.getInfo().title, ex.getMessage());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void addToQueue(AudioQueueExtendedInfo extendedInfo) {
        if (audioPlayer.startTrack(extendedInfo.audioTrack(), true)) return;
        trackQueue.offer(extendedInfo);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void nextTrack() {
        final AudioQueueExtendedInfo extendedInfo = trackQueue.poll();
        if (Objects.isNull(extendedInfo)) return;
        audioPlayer.startTrack(extendedInfo.audioTrack(), false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Queue<AudioQueueExtendedInfo> getTrackQueue() {
        return trackQueue;
    }

    AudioTrack getPausedTrack() {
        return pausedTrack;
    }

    String getTrackPositionInQueue() {
        if (trackQueue.size() == 1) return config.getLocaleText(LocaleSet.NEXT_TRACK_INDEX_MESS);
        return Integer.toString(trackQueue.size());
    }

    boolean isRepeating() {
        return repeating;
    }

    void setCountOfRepeats(int countOfRepeats) {
        this.countOfRepeats = countOfRepeats;
    }

    void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
}
