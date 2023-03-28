/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioLoaderResultImpl.java
 * Last modified: 19/03/2023, 20:52
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

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

import java.util.List;
import java.util.stream.Collectors;

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.dto.TrackEmbedContent;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.PlaylistEmbedContent;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
class AudioLoaderResultImpl implements AudioLoadResultHandler {

    private final boolean isUrlPattern;
    private final CommandEventWrapper deliveryEvent;

    private final BotConfiguration config;
    private final MusicManager musicManager;
    private final EmbedMessageBuilder builder;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    AudioLoaderResultImpl(
        MusicManager musicManager, BotConfiguration config, EmbedMessageBuilder builder, CommandEventWrapper deliveryEvent,
        boolean isUrlPattern
    ) {
        this.musicManager = musicManager;
        this.config = config;
        this.builder = builder;
        this.deliveryEvent = deliveryEvent;
        this.isUrlPattern = isUrlPattern;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void trackLoaded(AudioTrack track) {
        addNewAudioTrackToQueue(deliveryEvent.getDataSender(), track);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        final Member dataSender = deliveryEvent.getDataSender();
        final List<AudioTrack> trackList = playlist.getTracks();
        if (trackList.isEmpty()) return;

        if (isUrlPattern) {
            for (final AudioTrack track : trackList) {
                track.setUserData(dataSender);
                musicManager.getTrackScheduler().addToQueue(new AudioQueueExtendedInfo(dataSender, track));
            }
            final long durationsMilis = trackList.stream().mapToLong(AudioTrack::getDuration).sum();
            final String sumDurationTime = Utilities.convertMilisToDate(durationsMilis);
            final String thumbnailUrl = "https://img.youtube.com/vi/" + trackList.get(0).getInfo().identifier + "/0.jpg";

            final MessageEmbed messageEmbed = builder.createPlaylistTracksMessage(deliveryEvent,
                new PlaylistEmbedContent(Integer.toString(trackList.size()), sumDurationTime, thumbnailUrl));
            deliveryEvent.sendEmbedMessage(messageEmbed);

            JDALog.info(log, deliveryEvent, "New audio playlist: '%s' was added to queue", flattedTrackList(trackList));
        } else {
            addNewAudioTrackToQueue(dataSender, trackList.get(0));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void noMatches() {
        final MessageEmbed messageEmbed = builder.createErrorMessage(deliveryEvent,
            config.getLocaleText(LocaleSet.NOT_FOUND_TRACK_MESS), BugTracker.NOT_FOUND_TRACK);
        deliveryEvent.sendEmbedMessage(messageEmbed);

        JDALog.info(log, deliveryEvent, "Not available to find provided audio track/playlist");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void loadFailed(FriendlyException exception) {
        final MessageEmbed messageEmbed = builder.createErrorMessage(deliveryEvent,
            config.getLocaleText(LocaleSet.ISSUE_WHILE_LOADING_TRACK_MESS), BugTracker.ISSUE_ON_LOAD_TRACK);
        deliveryEvent.sendEmbedMessage(messageEmbed);

        JDALog.error(log, deliveryEvent, "Unexpected exception during load audio track/playlist: %s",
            exception.getMessage());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addNewAudioTrackToQueue(Member dataSender, AudioTrack track) {
        track.setUserData(dataSender);
        musicManager.getTrackScheduler().addToQueue(new AudioQueueExtendedInfo(dataSender, track));
        if (musicManager.getTrackScheduler().getTrackQueue().isEmpty()) return;

        final MessageEmbed messageEmbed = createSingleTrackEmbedMessage(track);
        deliveryEvent.sendEmbedMessage(messageEmbed);

        JDALog.info(log, deliveryEvent, "New audio track: '%s' was added to queue", track.getInfo().title);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MessageEmbed createSingleTrackEmbedMessage(AudioTrack track) {
        final String durationTime = Utilities.convertMilisToDate(track.getDuration());
        final String trackPos = musicManager.getTrackScheduler().getTrackPositionInQueue();
        final String thumbnailUrl = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";

        return builder.createSingleTrackMessage(deliveryEvent, new TrackEmbedContent(
            Utilities.getRichTrackTitle(track.getInfo()), durationTime, trackPos, thumbnailUrl));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String flattedTrackList(List<AudioTrack> audioTracks) {
        return audioTracks.stream().map(t -> t.getInfo().title).collect(Collectors.joining(", "));
    }
}
