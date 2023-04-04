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

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.misc.QueueAfterParam;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
public class TrackScheduler extends AudioEventAdapter {

    private final BotConfiguration config;
    private final EmbedMessageBuilder builder;

    @Getter(value = AccessLevel.PUBLIC)     private CommandEventWrapper deliveryEvent;
    @Getter(value = AccessLevel.PACKAGE)    private final AudioPlayer audioPlayer;
    @Getter(value = AccessLevel.PACKAGE)    private final SchedulerActions actions;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    TrackScheduler(
        BotConfiguration config, EmbedMessageBuilder builder, AudioPlayer audioPlayer, CommandEventWrapper deliveryEvent
    ) {
        this.config = config;
        this.builder = builder;
        this.audioPlayer = audioPlayer;
        this.deliveryEvent = deliveryEvent;
        this.actions = new SchedulerActions(this, config, builder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPlayerPause(AudioPlayer player) {
        if (Objects.isNull(audioPlayer.getPlayingTrack()) || actions.isOnClearing()) return;
        actions.setCurrentPausedTrack();

        final AudioTrackInfo trackInfo = audioPlayer.getPlayingTrack().getInfo();
        JDALog.info(log, deliveryEvent, "Audio track: '%s' was paused", trackInfo.title);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPlayerResume(AudioPlayer player) {
        if (Objects.isNull(actions.getPausedTrack()) || actions.isOnClearing()) return;
        actions.clearPausedTrack();

        final AudioTrackInfo trackInfo = audioPlayer.getPlayingTrack().getInfo();
        JDALog.info(log, deliveryEvent, "Paused audio track: '%s' was resumed", trackInfo.title);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (!Objects.isNull(actions.getThreadCountToLeave())) actions.cancelIdleThread();
        if (actions.isNextTrackInfoDisabled() || actions.isOnClearing()) return;

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
        if (actions.isInfiniteRepeating()) actions.setNextTrackInfoDisabled(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (actions.isOnClearing()) return;
        final boolean isNoneRepeating = !actions.isInfiniteRepeating() && actions.getCountOfRepeats() == 0;
        if (Objects.isNull(audioPlayer.getPlayingTrack()) && actions.getTrackQueue().isEmpty() && isNoneRepeating) {
            final MessageEmbed messageEmbed = builder.createMessage(LocaleSet.ON_END_PLAYBACK_QUEUE_MESS);
            deliveryEvent.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
            JDALog.info(log, deliveryEvent, "End of playing queue tracks");

            actions.setNextTrackInfoDisabled(false);
            actions.leaveAndSendMessageAfterInactivity();
            return;
        }
        if (actions.isInfiniteRepeating()) {
            audioPlayer.startTrack(track.makeClone(), false);
            return;
        }
        if (actions.isInfinitePlaylistRepeating()) {
            final Member sender = (Member) track.getUserData();
            actions.addToQueue(new AudioQueueExtendedInfo(sender, track.makeClone()));
            if (endReason.mayStartNext) actions.nextTrack();
            return;
        }
        if (actions.getCountOfRepeats() > 0) {
            final AudioTrackInfo trackInfo = track.getInfo();
            final int currentRepeat = actions.getCurrentRepeat();

            final MessageEmbed messageEmbed = builder
                .createMessage(LocaleSet.MULTIPLE_REPEATING_TRACK_INFO_MESS, Map.of(
                    "currentRepeat", currentRepeat,
                    "track", String.format("[%s](%s)", trackInfo.title, trackInfo.uri),
                    "elapsedRepeats", actions.decreaseCountOfRepeats()
                ));
            audioPlayer.startTrack(track.makeClone(), false);
            deliveryEvent.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
            actions.setNextTrackInfoDisabled(true);
            JDALog.info(log, deliveryEvent, "Repeat %s times of track '%s' from elapsed %s repeats",
                currentRepeat, trackInfo.title, actions.getCountOfRepeats());
            return;
        }
        if (endReason.mayStartNext) {
            actions.nextTrack();
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

    public void setDeliveryEvent(CommandEventWrapper event) {
        this.deliveryEvent = event;
    }
}
