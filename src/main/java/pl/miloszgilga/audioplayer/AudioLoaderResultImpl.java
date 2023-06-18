/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioLoaderResultImpl.java
 * Last modified: 16/05/2023, 18:51
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
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.TrackEmbedContent;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.PlaylistEmbedContent;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.vote.IVoteSequencer;
import pl.miloszgilga.vote.SongChooserConfigData;
import pl.miloszgilga.vote.SongChooserSystemSequencer;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
class AudioLoaderResultImpl implements AudioLoadResultHandler {

    private final boolean isUrlPattern;
    private final CommandEventWrapper deliveryEvent;

    private final BotConfiguration config;
    private final MusicManager musicManager;
    private final RemotePropertyHandler handler;
    private final EmbedMessageBuilder builder;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    AudioLoaderResultImpl(
        MusicManager musicManager, BotConfiguration config, RemotePropertyHandler handler, EmbedMessageBuilder builder,
        CommandEventWrapper deliveryEvent, boolean isUrlPattern
    ) {
        this.musicManager = musicManager;
        this.config = config;
        this.handler = handler;
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
                musicManager.getActions().addToQueueAndOffer(new AudioQueueExtendedInfo(dataSender, track));
            }
            final long durationsMilis = trackList.stream().mapToLong(AudioTrack::getDuration).sum();
            final String sumDurationTime = Utilities.convertMilisToDate(durationsMilis);
            final String thumbnailUrl = "https://img.youtube.com/vi/" + trackList.get(0).getInfo().identifier + "/0.jpg";

            final MessageEmbed messageEmbed = builder.createPlaylistTracksMessage(deliveryEvent,
                new PlaylistEmbedContent(Integer.toString(trackList.size()), sumDurationTime, thumbnailUrl));
            deliveryEvent.sendEmbedMessage(messageEmbed);

            JDALog.info(log, deliveryEvent, "New audio playlist: '%s' was added to queue", flattedTrackList(trackList));
        } else {
            final var configData = new SongChooserConfigData(config, deliveryEvent, handler, builder,
                track -> addNewAudioTrackToQueue(dataSender, track));
            final IVoteSequencer sequencer = new SongChooserSystemSequencer(trackList, configData);
            sequencer.initializeAndStart();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void noMatches() {
        final MessageEmbed messageEmbed = builder.createErrorMessage(deliveryEvent,
            config.getLocaleText(ResLocaleSet.NOT_FOUND_TRACK_MESS, deliveryEvent.getGuild()), BugTracker.NOT_FOUND_TRACK);
        deliveryEvent.sendEmbedMessage(messageEmbed);

        JDALog.info(log, deliveryEvent, "Not available to find provided audio track/playlist");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void loadFailed(FriendlyException exception) {
        final MessageEmbed messageEmbed = builder.createErrorMessage(deliveryEvent,
            config.getLocaleText(ResLocaleSet.ISSUE_WHILE_LOADING_TRACK_MESS, deliveryEvent.getGuild()),
            BugTracker.ISSUE_ON_LOAD_TRACK);
        deliveryEvent.sendEmbedMessage(messageEmbed);

        JDALog.error(log, deliveryEvent, "Unexpected exception during load audio track/playlist: %s",
            exception.getMessage());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addNewAudioTrackToQueue(Member dataSender, AudioTrack track) {
        track.setUserData(dataSender);
        musicManager.getActions().addToQueueAndOffer(new AudioQueueExtendedInfo(dataSender, track));
        if (musicManager.getQueue().isEmpty()) return;

        final MessageEmbed messageEmbed = createSingleTrackEmbedMessage(track);
        deliveryEvent.sendEmbedMessage(messageEmbed);

        JDALog.info(log, deliveryEvent, "New audio track: '%s' was added to queue", track.getInfo().title);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MessageEmbed createSingleTrackEmbedMessage(AudioTrack track) {
        final String durationTime = Utilities.convertMilisToDate(track.getDuration());
        final String trackPos = musicManager.getActions().getTrackPositionInQueue();
        final String thumbnailUrl = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";

        return builder.createSingleTrackMessage(deliveryEvent, new TrackEmbedContent(
            Utilities.getRichTrackTitle(track.getInfo()), durationTime, trackPos, thumbnailUrl));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String flattedTrackList(List<AudioTrack> audioTracks) {
        return audioTracks.stream().map(t -> t.getInfo().title).collect(Collectors.joining(", "));
    }
}
