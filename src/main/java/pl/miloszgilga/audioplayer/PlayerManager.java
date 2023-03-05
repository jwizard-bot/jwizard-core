/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlayerManager.java
 * Last modified: 04/03/2023, 22:39
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

import net.dv8tion.jda.api.entities.Guild;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import org.springframework.stereotype.Component;
import org.apache.http.client.config.RequestConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.AudioPlayerException.TrackIsNotPausedException;
import static pl.miloszgilga.exception.AudioPlayerException.TrackIsNotPlayingException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class PlayerManager extends DefaultAudioPlayerManager implements IPlayerManager {

    private static final int CONNECTION_TIMEOUT = 10000;

    private final BotConfiguration config;
    private final Map<Long, MusicManager> musicManagers = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    PlayerManager(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initialize() {
        AudioSourceManagers.registerLocalSource(this);
        AudioSourceManagers.registerRemoteSources(this);
        setHttpRequestConfigurator(config -> RequestConfig.copy(config).setConnectTimeout(CONNECTION_TIMEOUT).build());
        source(YoutubeAudioSourceManager.class)
            .setPlaylistPageCount(config.getProperty(BotProperty.J_PAGINATION_MAX, Integer.class));
        log.info("Player manager was successfuly initialized.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void loadAndPlay(CommandEvent event, String trackUrl) {
        final MusicManager musicManager = getMusicManager(event.getGuild());
        final AudioLoadResultHandler audioLoadResultHandler = new AudioLoaderResultImpl(musicManager, config);
        this.loadItemOrdered(musicManager, trackUrl, audioLoadResultHandler);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void pauseCurrentTrack(CommandEvent event) {
        final MusicManager musicManager = getMusicManager(event.getGuild());
        if (Objects.isNull(musicManager.getAudioPlayer().getPlayingTrack())) {
            throw new TrackIsNotPlayingException(config, new EventWrapper(event));
        }
        musicManager.getAudioPlayer().setPaused(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void resumeCurrentTrack(CommandEvent event) {
        final MusicManager musicManager = getMusicManager(event.getGuild());
        if (Objects.isNull(musicManager.getTrackScheduler().getPausedTrack())) {
            throw new TrackIsNotPausedException(config, new EventWrapper(event));
        }
        musicManager.getAudioPlayer().setPaused(false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void toggleRepeatCurrentTrack(CommandEvent event) {
        final MusicManager musicManager = getMusicManager(event.getGuild());
        if (Objects.isNull(musicManager.getAudioPlayer().getPlayingTrack())) {
            throw new TrackIsNotPlayingException(config, new EventWrapper(event));
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MusicManager getMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            final MusicManager musicManager = new MusicManager(this, config);
            guild.getAudioManager().setSendingHandler(musicManager.getAudioPlayerSendHandler());
            return musicManager;
        });
    }
}
