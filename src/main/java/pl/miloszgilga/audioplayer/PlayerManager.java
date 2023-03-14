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

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
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
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.AudioPlayerException.TrackIsNotPausedException;
import static pl.miloszgilga.exception.AudioPlayerException.TrackIsNotPlayingException;
import static pl.miloszgilga.exception.AudioPlayerException.InvokerIsNotTrackSenderOrAdminException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class PlayerManager extends DefaultAudioPlayerManager implements IPlayerManager {

    private static final int CONNECTION_TIMEOUT = 10000;

    private final BotConfiguration config;
    private final EmbedMessageBuilder builder;
    private final Map<Long, MusicManager> musicManagers = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    PlayerManager(BotConfiguration config, EmbedMessageBuilder builder) {
        this.config = config;
        this.builder = builder;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initialize() {
        AudioSourceManagers.registerLocalSource(this);
        AudioSourceManagers.registerRemoteSources(this);
        setHttpRequestConfigurator(config -> RequestConfig.copy(config).setConnectTimeout(CONNECTION_TIMEOUT).build());
        source(YoutubeAudioSourceManager.class)
            .setPlaylistPageCount(config.getProperty(BotProperty.J_PAGINATION_MAX, Integer.class));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void loadAndPlay(CommandEvent event, String trackUrl, boolean isUrlPattern) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioLoadResultHandler audioLoadResultHandler = new AudioLoaderResultImpl(musicManager, config,
            builder, new EventWrapper(event), isUrlPattern);
        event.getGuild().getAudioManager().setSelfDeafened(true);
        loadItemOrdered(musicManager, trackUrl, audioLoadResultHandler);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void pauseCurrentTrack(CommandEvent event) {
        final MusicManager musicManager = checkPermissions(event);
        musicManager.getAudioPlayer().setPaused(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void resumeCurrentTrack(CommandEvent event) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioTrack pausedTrack = musicManager.getTrackScheduler().getPausedTrack();
        if (Objects.isNull(pausedTrack)) {
            throw new TrackIsNotPausedException(config, new EventWrapper(event));
        }
        if (invokerIsNotTrackSenderOrAdmin(pausedTrack, event)) {
            throw new InvokerIsNotTrackSenderOrAdminException(config, new EventWrapper(event));
        }
        musicManager.getAudioPlayer().setPaused(false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void repeatCurrentTrack(CommandEvent event, int countOfRepeats) {
        final MusicManager musicManager = checkPermissions(event);
        final EventWrapper eventWrapper = new EventWrapper(event);
        musicManager.getTrackScheduler().setCountOfRepeats(countOfRepeats);
        if (countOfRepeats == 0) {
            log.info("G: {}, A: {} <> Repeating of current playing track '{}' was removed", eventWrapper.guildName(),
                eventWrapper.authorTag(), getCurrentPlayingTrack(event).title);
            return;
        }
        log.info("G: {}, A: {} <> Current playing track '{}' will be repeating {}x times",
            eventWrapper.guildName(), eventWrapper.authorTag(), getCurrentPlayingTrack(event).title, countOfRepeats);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean toggleInfiniteLoopCurrentTrack(CommandEvent event) {
        final MusicManager musicManager = checkPermissions(event);
        musicManager.getTrackScheduler().setInfiniteRepeating(!musicManager.getTrackScheduler().isInfiniteRepeating());
        final boolean isRepeating = musicManager.getTrackScheduler().isInfiniteRepeating();
        final EventWrapper eventWrapper = new EventWrapper(event);
        if (isRepeating) {
            log.info("G: {}, A: {} <> Current playing track '{}' has been placed in infinite loop",
                eventWrapper.guildName(), eventWrapper.authorTag(), getCurrentPlayingTrack(event).title);
        } else {
            log.info("G: {}, A: {} <> Current playing track '{}' has been removed from infinite loop",
                eventWrapper.guildName(), eventWrapper.authorTag(), getCurrentPlayingTrack(event).title);
        }
        return musicManager.getTrackScheduler().isInfiniteRepeating();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setPlayerVolume(CommandEvent event, int volume) {
        final MusicManager musicManager = getMusicManager(event);
        final EventWrapper eventWrapper = new EventWrapper(event);
        musicManager.getAudioPlayer().setVolume(volume);
        log.info("G: {}, A: {} <> Audio player volume was set to '{}' volume units", eventWrapper.guildName(),
            eventWrapper.authorTag(), volume);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MusicManager checkPermissions(CommandEvent event) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioTrack playingTrack = musicManager.getAudioPlayer().getPlayingTrack();
        if (Objects.isNull(playingTrack)) {
            throw new TrackIsNotPlayingException(config, new EventWrapper(event));
        }
        if (invokerIsNotTrackSenderOrAdmin(playingTrack, event)) {
            throw new InvokerIsNotTrackSenderOrAdminException(config, new EventWrapper(event));
        }
        return musicManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean invokerIsNotTrackSenderOrAdmin(AudioTrack track, CommandEvent event) {
        final User dataSender = ((Member)track.getUserData()).getUser();
        final Member messageSender = event.getGuild().getMember(event.getAuthor());
        if (Objects.isNull(messageSender)) return true;

        final String djRoleName = config.getProperty(BotProperty.J_DJ_ROLE_NAME);
        final boolean isNotOwner = !event.getAuthor().getId().equals(event.getClient().getOwnerId());
        final boolean isNotManager = !event.getMember().hasPermission(Permission.MANAGE_SERVER);
        final boolean isNotDj = event.getMember().getRoles().stream().noneMatch(r -> r.getName().equals(djRoleName));

        return !dataSender.getAsTag().equals(event.getAuthor().getAsTag()) && !isNotOwner && !isNotManager && !isNotDj;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MusicManager getMusicManager(CommandEvent event) {
        return musicManagers.computeIfAbsent(event.getGuild().getIdLong(), guildId -> {
            final MusicManager musicManager = new MusicManager(this, builder, config,
                event.getGuild(), new EventWrapper(event));
            event.getGuild().getAudioManager().setSendingHandler(musicManager.getAudioPlayerSendHandler());
            return musicManager;
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MusicManager getMusicManager(Guild guild) {
        return musicManagers.get(guild.getIdLong());
    }

    public AudioTrackInfo getCurrentPlayingTrack(CommandEvent event) {
        return getMusicManager(event).getAudioPlayer().getPlayingTrack().getInfo();
    }
}
