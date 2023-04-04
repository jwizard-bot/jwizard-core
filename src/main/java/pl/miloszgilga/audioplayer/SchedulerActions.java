/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SchedulerActions.java
 * Last modified: 02/04/2023, 23:46
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
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.audioplayer;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.dto.TrackPosition;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
public class SchedulerActions {

    private final AudioPlayer audioPlayer;
    private final BotConfiguration config;
    private final EmbedMessageBuilder builder;
    private final CommandEventWrapper deliveryEvent;

    @Getter(value = AccessLevel.PACKAGE)    private final Queue<AudioQueueExtendedInfo> trackQueue = new LinkedList<>();

    @Getter(value = AccessLevel.PUBLIC)     private AudioTrack pausedTrack;
    @Getter(value = AccessLevel.PACKAGE)    private boolean onClearing = false;
    @Getter(value = AccessLevel.PACKAGE)    private boolean infiniteRepeating = false;
    @Getter(value = AccessLevel.PACKAGE)    private boolean infinitePlaylistRepeating = false;
    @Getter(value = AccessLevel.PACKAGE)    private int countOfRepeats = 0;
    @Getter(value = AccessLevel.PACKAGE)    private boolean nextTrackInfoDisabled = false;
    @Getter(value = AccessLevel.PACKAGE)    private ScheduledFuture<?> threadCountToLeave;

    private int totalCountOfRepeats = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    SchedulerActions(TrackScheduler trackScheduler, BotConfiguration config, EmbedMessageBuilder builder) {
        this.audioPlayer = trackScheduler.getAudioPlayer();
        this.deliveryEvent = trackScheduler.getDeliveryEvent();
        this.config = config;
        this.builder = builder;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void addToQueueAndOffer(AudioQueueExtendedInfo extendedInfo) {
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

    public void skipToPosition(int position) {
        AudioQueueExtendedInfo extendedInfo = null;
        for (int i = 0; i < position; i++) {
            extendedInfo = trackQueue.poll();
        }
        if (Objects.isNull(extendedInfo)) return;
        audioPlayer.startTrack(extendedInfo.audioTrack(), false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AudioTrack moveToPosition(TrackPosition positions) {
        final List<AudioQueueExtendedInfo> copyTracks = new ArrayList<>(trackQueue);
        final AudioQueueExtendedInfo selectedTrack = copyTracks.remove(positions.previous() - 1);
        copyTracks.add(positions.selected() - 1, selectedTrack);
        trackQueue.clear();
        trackQueue.addAll(copyTracks);
        return selectedTrack.audioTrack();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    public boolean checkIfAllTrackOrTracksIsFromSelectedMember(Member member) {
        if (trackQueue.isEmpty()) {
            if (Objects.isNull(audioPlayer.getPlayingTrack())) return false;
            final Member sender = (Member) audioPlayer.getPlayingTrack().getUserData();
            return sender.getId().equals(member.getId());
        }
        return trackQueue.stream().allMatch(t -> t.sender().getId().equals(member.getId()));
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
        infinitePlaylistRepeating = false;

        if (showMessage) {
            final MessageEmbed messageEmbed = builder.createMessage(LocaleSet.LEAVE_EMPTY_CHANNEL_MESS);
            deliveryEvent.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
        }
        onClearing = false;
        JDALog.info(log, deliveryEvent, "Remove playing track and clear queue");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void closeAudioConnection() {
        final Guild guild = deliveryEvent.getDataSender().getGuild();
        config.getThreadPool().submit(() -> guild.getAudioManager().closeAudioConnection());
        JDALog.info(log, deliveryEvent, "Audio connection threadpool was closed");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void leaveAndSendMessageAfterInactivity() {
        final int timeToLeaveChannel = config.getProperty(BotProperty.J_INACTIVITY_NO_TRACK_TIMEOUT, Integer.class);
        threadCountToLeave = config.getThreadPool().schedule(() -> {
            final MessageEmbed leaveMessageEmbed = builder
                .createMessage(LocaleSet.LEAVE_END_PLAYBACK_QUEUE_MESS, Map.of(
                    "elapsed", Utilities.convertSecondsToMinutes(timeToLeaveChannel)
                ));
            clearAndDestroy(false);
            closeAudioConnection();

            deliveryEvent.getTextChannel().sendMessageEmbeds(leaveMessageEmbed).queue();
            JDALog.info(log, deliveryEvent, "Leave voice channel after '%s' seconds of inactivity", timeToLeaveChannel);
        }, timeToLeaveChannel, TimeUnit.SECONDS);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getTrackPositionInQueue() {
        if (trackQueue.size() == 1) return config.getLocaleText(LocaleSet.NEXT_TRACK_INDEX_MESS);
        return Integer.toString(trackQueue.size());
    }

    public AudioTrack getTrackByPosition(int position) {
        final List<AudioQueueExtendedInfo> audioTracks = new ArrayList<>(trackQueue);
        return audioTracks.get(position - 1).audioTrack();
    }

    public long getAverageTrackDuration() {
        return (long) (trackQueue.stream().mapToLong(t -> t.audioTrack().getDuration()).average().orElse(0));
    }

    public boolean checkInvTrackPosition(int position) {
        return position <= 0 || position > trackQueue.size();
    }

    boolean checkIfMemberAddAnyTracksToQueue(Member member) {
        return trackQueue.stream().anyMatch(t -> {
            final Member iterateMember = (Member) t.audioTrack().getUserData();
            return member.equals(iterateMember);
        });
    }

    boolean toggleInfinitePlaylistRepeating() {
        infinitePlaylistRepeating = !infinitePlaylistRepeating;
        return infinitePlaylistRepeating;
    }

    void setCountOfRepeats(int countOfRepeats) {
        this.countOfRepeats = countOfRepeats;
        totalCountOfRepeats = countOfRepeats;
        if (countOfRepeats > 0) {
            nextTrackInfoDisabled = true;
        }
    }

    void clearQueue()                                               { trackQueue.clear(); }
    public int getQueueSize()                                       { return trackQueue.size(); }
    void cancelIdleThread()                                         { threadCountToLeave.cancel(true); }
    void addToQueue(AudioQueueExtendedInfo track)                   { trackQueue.add(track); }
    void setCurrentPausedTrack()                                    { pausedTrack = audioPlayer.getPlayingTrack(); }
    void clearPausedTrack()                                         { pausedTrack = null; }
    void setInfiniteRepeating(boolean infiniteRepeating)            { this.infiniteRepeating = infiniteRepeating; }
    int decreaseCountOfRepeats()                                    { return --countOfRepeats; }
    int getCurrentRepeat()                                          { return (totalCountOfRepeats - countOfRepeats) + 1; }
    void setNextTrackInfoDisabled(boolean nextTrackInfoDisabled)    { this.nextTrackInfoDisabled = nextTrackInfoDisabled; }
}
