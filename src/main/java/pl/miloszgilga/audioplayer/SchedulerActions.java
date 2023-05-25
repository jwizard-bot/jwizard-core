/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SchedulerActions.java
 * Last modified: 17/05/2023, 01:34
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
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
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.TrackPosition;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
public class SchedulerActions {

    private final AudioPlayer audioPlayer;
    private final BotConfiguration config;
    private final EmbedMessageBuilder builder;
    private final CommandEventWrapper deliveryEvent;
    private final RemotePropertyHandler handler;

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

    SchedulerActions(
        TrackScheduler trackScheduler, BotConfiguration config, EmbedMessageBuilder builder, RemotePropertyHandler handler
    ) {
        this.audioPlayer = trackScheduler.getAudioPlayer();
        this.deliveryEvent = trackScheduler.getDeliveryEvent();
        this.config = config;
        this.builder = builder;
        this.handler = handler;
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
            final MessageEmbed messageEmbed = builder.createMessage(ResLocaleSet.LEAVE_EMPTY_CHANNEL_MESS,
                deliveryEvent.getGuild());
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
        final int timeToLeaveChannel = handler.getPossibleRemoteProperty(RemoteProperty.R_INACTIVITY_NO_TRACK_TIMEOUT,
            deliveryEvent.getGuild(), Integer.class);
        threadCountToLeave = config.getThreadPool().schedule(() -> {
            final MessageEmbed leaveMessageEmbed = builder
                .createMessage(ResLocaleSet.LEAVE_END_PLAYBACK_QUEUE_MESS, Map.of(
                    "elapsed", Utilities.convertSecondsToMinutes(timeToLeaveChannel)
                ), deliveryEvent.getGuild());
            clearAndDestroy(false);
            closeAudioConnection();

            deliveryEvent.getTextChannel().sendMessageEmbeds(leaveMessageEmbed).queue();
            JDALog.info(log, deliveryEvent, "Leave voice channel after '%s' seconds of inactivity", timeToLeaveChannel);
        }, timeToLeaveChannel, TimeUnit.SECONDS);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getTrackPositionInQueue() {
        if (trackQueue.size() == 1) return config.getLocaleText(ResLocaleSet.NEXT_TRACK_INDEX_MESS, deliveryEvent.getGuild());
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
