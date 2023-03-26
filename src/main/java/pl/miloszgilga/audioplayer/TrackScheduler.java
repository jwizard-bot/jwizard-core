/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: TrackScheduler.java
 * Last modified: 23/03/2023, 00:52
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided “as is”, without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.audioplayer;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.misc.QueueAfterParam;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
public class TrackScheduler extends AudioEventAdapter {

    private final BotConfiguration config;
    private final EmbedMessageBuilder builder;
    private final AudioPlayer audioPlayer;

    private final Queue<AudioQueueExtendedInfo> trackQueue = new LinkedList<>();

    private AudioTrack pausedTrack;
    private CommandEventWrapper deliveryEvent;
    private ScheduledFuture<?> threadCountToLeave;
    private int countOfRepeats = 0;
    private int totalCountOfRepeats = 0;
    private boolean nextTrackInfoDisabled = false;
    private boolean infiniteRepeating = false;
    private boolean onClearing = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    TrackScheduler(
        BotConfiguration config, EmbedMessageBuilder builder, AudioPlayer audioPlayer, CommandEventWrapper deliveryEvent
    ) {
        this.config = config;
        this.builder = builder;
        this.audioPlayer = audioPlayer;
        this.deliveryEvent = deliveryEvent;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPlayerPause(AudioPlayer player) {
        if (Objects.isNull(audioPlayer.getPlayingTrack()) || onClearing) return;
        pausedTrack = audioPlayer.getPlayingTrack();

        final AudioTrackInfo trackInfo = audioPlayer.getPlayingTrack().getInfo();
        JDALog.info(log, deliveryEvent, "Audio track: '%s' was paused", trackInfo.title);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPlayerResume(AudioPlayer player) {
        if (Objects.isNull(pausedTrack) || onClearing) return;
        pausedTrack = null;

        final AudioTrackInfo trackInfo = audioPlayer.getPlayingTrack().getInfo();
        JDALog.info(log, deliveryEvent, "Paused audio track: '%s' was resumed", trackInfo.title);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (!Objects.isNull(threadCountToLeave)) threadCountToLeave.cancel(true);
        if (nextTrackInfoDisabled || onClearing) return;

        final ExtendedAudioTrackInfo trackInfo = new ExtendedAudioTrackInfo(audioPlayer.getPlayingTrack());
        final MessageEmbed messageEmbed;

        if (audioPlayer.isPaused()) {
            messageEmbed = builder.createTrackMessage(LocaleSet.ON_TRACK_START_ON_PAUSED_MESS, Map.of(
                "track", String.format("[%s](%s)", trackInfo.title, trackInfo.uri),
                "resumeCmd", BotCommand.RESUME_TRACK.parseWithPrefix(config)
            ), trackInfo.getThumbnailUrl());
            JDALog.info(log, deliveryEvent, "Staring playing audio track: '%s' when audio player is paused",
                trackInfo.title);
        } else {
            messageEmbed = builder.createTrackMessage(LocaleSet.ON_TRACK_START_MESS, Map.of(
                "track", String.format("[%s](%s)", trackInfo.title, trackInfo.uri)
            ), trackInfo.getThumbnailUrl());
            JDALog.info(log, deliveryEvent, "Staring playing audio track: '%s'", trackInfo.title);
        }
        deliveryEvent.sendEmbedMessage(messageEmbed, new QueueAfterParam(1, TimeUnit.SECONDS));
        if (infiniteRepeating) nextTrackInfoDisabled = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (onClearing) return;
        final boolean isNoneRepeating = !infiniteRepeating && countOfRepeats == 0;
        if (Objects.isNull(audioPlayer.getPlayingTrack()) && trackQueue.isEmpty() && isNoneRepeating) {
            final MessageEmbed messageEmbed = builder.createMessage(LocaleSet.ON_END_PLAYBACK_QUEUE_MESS);
            deliveryEvent.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
            JDALog.info(log, deliveryEvent, "End of playing queue tracks");

            nextTrackInfoDisabled = false;
            final int timeToLeaveChannel = config.getProperty(BotProperty.J_INACTIVITY_NO_TRACK_TIMEOUT, Integer.class);
            threadCountToLeave = config.getThreadPool().schedule(() -> {
                final MessageEmbed leaveMessageEmbed = builder
                    .createMessage(LocaleSet.LEAVE_END_PLAYBACK_QUEUE_MESS, Map.of(
                        "elapsed", Utilities.convertSecondsToMinutes(timeToLeaveChannel)
                    ));
                closeAudioConnection();
                audioPlayer.setPaused(false);

                deliveryEvent.getTextChannel().sendMessageEmbeds(leaveMessageEmbed).queue();
                JDALog.info(log, deliveryEvent, "Leave voice channel after '%s' seconds of inactivity", timeToLeaveChannel);
            }, timeToLeaveChannel, TimeUnit.SECONDS);
            return;
        }
        if (infiniteRepeating) {
            audioPlayer.startTrack(track.makeClone(), false);
        } else if (countOfRepeats > 0) {
            final AudioTrackInfo trackInfo = track.getInfo();
            final int currentRepeat = (totalCountOfRepeats - countOfRepeats) + 1;

            final MessageEmbed messageEmbed = builder
                .createMessage(LocaleSet.MULTIPLE_REPEATING_TRACK_INFO_MESS, Map.of(
                    "currentRepeat", currentRepeat,
                    "track", String.format("[%s](%s)", trackInfo.title, trackInfo.uri),
                    "elapsedRepeats", --countOfRepeats
                ));
            audioPlayer.startTrack(track.makeClone(), false);
            deliveryEvent.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
            nextTrackInfoDisabled = true;
            JDALog.info(log, deliveryEvent, "Repeat %s times of track '%s' from elapsed %s repeats",
                currentRepeat, trackInfo.title, countOfRepeats);
        } else if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException ex) {
        final MessageEmbed messageEmbed = builder.createErrorMessage(deliveryEvent,
            config.getLocaleText(LocaleSet.ISSUE_WHILE_PLAYING_TRACK_MESS), BugTracker.ISSUE_WHILE_PLAYING_TRACK);
        deliveryEvent.sendEmbedMessage(messageEmbed);
        JDALog.error(log, deliveryEvent, "Unexpected issue while playing track: '%s'. Cause: %s", ex.getMessage());
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

    public void skipToPosition(int position) {
        AudioQueueExtendedInfo extendedInfo = null;
        for (int i = 0; i < position; i++) {
            extendedInfo = trackQueue.poll();
        }
        if (Objects.isNull(extendedInfo)) return;
        audioPlayer.startTrack(extendedInfo.audioTrack(), false);
    }

    public List<ExtendedAudioTrackInfo> removeAllTracksFromMember(Member member) {
        final List<ExtendedAudioTrackInfo> removedTracks = new ArrayList<>();
        final List<AudioQueueExtendedInfo> toRemove = new ArrayList<>(trackQueue);
        for (final AudioQueueExtendedInfo track : toRemove) {
            if (!track.sender().equals(member)) continue;

            final AudioQueueExtendedInfo extendedInfo = trackQueue.poll();
            if (Objects.isNull(extendedInfo)) continue;
            removedTracks.add(new ExtendedAudioTrackInfo(extendedInfo.audioTrack()));
        }
        return removedTracks;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void clearAndDestroy(boolean showMessage) {
        onClearing = true;
        audioPlayer.setPaused(false);
        audioPlayer.stopTrack();
        trackQueue.clear();

        pausedTrack = null;
        countOfRepeats = 0;
        totalCountOfRepeats = 0;
        infiniteRepeating = false;
        nextTrackInfoDisabled = false;

        if (showMessage) {
            final MessageEmbed messageEmbed = builder.createMessage(LocaleSet.LEAVE_EMPTY_CHANNEL_MESS);
            deliveryEvent.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
        }
        onClearing = false;
        JDALog.info(log, deliveryEvent, "Remove playing track and clear queue");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void closeAudioConnection() {
        final Guild guild = deliveryEvent.getDataSender().getGuild();
        config.getThreadPool().submit(() -> guild.getAudioManager().closeAudioConnection());
        JDALog.info(log, deliveryEvent, "Audio connection threadpool closed");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Queue<AudioQueueExtendedInfo> getTrackQueue() {
        return trackQueue;
    }

    public AudioTrack getPausedTrack() {
        return pausedTrack;
    }

    String getTrackPositionInQueue() {
        if (trackQueue.size() == 1) return config.getLocaleText(LocaleSet.NEXT_TRACK_INDEX_MESS);
        return Integer.toString(trackQueue.size());
    }

    boolean isInfiniteRepeating() {
        return infiniteRepeating;
    }

    public CommandEventWrapper getDeliveryEvent() {
        return deliveryEvent;
    }

    public void setDeliveryEvent(CommandEventWrapper event) {
        this.deliveryEvent = event;
    }

    public long getAverageTrackDuration() {
        return (long) (trackQueue.stream().mapToLong(t -> t.audioTrack().getDuration()).average().orElse(0));
    }

    public boolean checkTrackPosition(int position) {
        return position > 0 && position <= trackQueue.size();
    }

    public boolean checkIfMemberAddAnyTracksToQueue(Member member) {
        return trackQueue.stream().anyMatch(t -> {
            final Member iterateMember = (Member) t.audioTrack().getUserData();
            return member.equals(iterateMember);
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void setCountOfRepeats(int countOfRepeats) {
        this.countOfRepeats = countOfRepeats;
        totalCountOfRepeats = countOfRepeats;
        if (countOfRepeats > 0) {
            nextTrackInfoDisabled = true;
        }
    }

    void setInfiniteRepeating(boolean infiniteRepeating) {
        this.infiniteRepeating = infiniteRepeating;
    }
}
