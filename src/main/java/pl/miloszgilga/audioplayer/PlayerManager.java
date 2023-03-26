/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlayerManager.java
 * Last modified: 26/03/2023, 03:09
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

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import org.springframework.stereotype.Component;
import org.apache.http.client.config.RequestConfig;

import java.util.*;

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.misc.ValidateUserDetails;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.MemberRemovedTracksInfo;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.AudioPlayerException.TrackIsNotPlayingException;
import static pl.miloszgilga.exception.AudioPlayerException.TrackQueueIsEmptyException;
import static pl.miloszgilga.exception.AudioPlayerException.UserNotAddedTracksToQueueException;
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
    public void loadAndPlay(CommandEventWrapper event, String trackUrl, boolean isUrlPattern) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioLoadResultHandler audioLoadResultHandler = new AudioLoaderResultImpl(musicManager, config,
            builder, event, isUrlPattern);
        event.getGuild().getAudioManager().setSelfDeafened(true);
        loadItemOrdered(musicManager, trackUrl, audioLoadResultHandler);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void pauseCurrentTrack(CommandEventWrapper event) {
        final MusicManager musicManager = checkPermissions(event);
        musicManager.getAudioPlayer().setPaused(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void resumeCurrentTrack(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioTrack pausedTrack = musicManager.getTrackScheduler().getPausedTrack();
        if (invokerIsNotTrackSenderOrAdmin(pausedTrack, event)) {
            throw new InvokerIsNotTrackSenderOrAdminException(config, event);
        }
        musicManager.getAudioPlayer().setPaused(false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public AudioTrackInfo skipCurrentTrack(CommandEventWrapper event) {
        final MusicManager musicManager = checkPermissions(event);
        final AudioTrackInfo skippedTrack = getCurrentPlayingTrack(event);
        if (musicManager.getTrackScheduler().getTrackQueue().isEmpty()) {
            musicManager.getAudioPlayer().stopTrack();
        } else {
            musicManager.getTrackScheduler().nextTrack();
        }
        JDALog.info(log, event, "Current playing track '%s' was skipped", skippedTrack.title);
        return skippedTrack;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void shuffleQueue(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        Collections.shuffle((List<?>)musicManager.getQueue());
        JDALog.info(log, event, "Current queue tracks was shuffled");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void repeatCurrentTrack(CommandEventWrapper event, int countOfRepeats) {
        final MusicManager musicManager = checkPermissions(event);
        musicManager.getTrackScheduler().setCountOfRepeats(countOfRepeats);
        if (countOfRepeats == 0) {
            JDALog.info(log, event, "Repeating of current playing track '%s' was removed",
                getCurrentPlayingTrack(event).title);
            return;
        }
        JDALog.info(log, event, "Current playing track '%s' will be repeating %s times",
            getCurrentPlayingTrack(event).title, String.valueOf(countOfRepeats));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean toggleInfiniteLoopCurrentTrack(CommandEventWrapper event) {
        final MusicManager musicManager = checkPermissions(event);
        musicManager.getTrackScheduler().setInfiniteRepeating(!musicManager.getTrackScheduler().isInfiniteRepeating());
        final boolean isRepeating = musicManager.getTrackScheduler().isInfiniteRepeating();
        final String currentTrackTitle = getCurrentPlayingTrack(event).title;
        if (isRepeating) {
            JDALog.info(log, event, "Current playing track '%s' has been placed in infinite loop", currentTrackTitle);
        } else {
            JDALog.info(log, event, "Current playing track '%s' has been removed from infinite loop", currentTrackTitle);
        }
        return musicManager.getTrackScheduler().isInfiniteRepeating();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setPlayerVolume(CommandEventWrapper event, int volume) {
        final MusicManager musicManager = getMusicManager(event);
        musicManager.getAudioPlayer().setVolume(volume);
        JDALog.info(log, event, "Audio player volume was set to '%s' volume units", volume);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public AudioTrack skipToTrackPos(CommandEventWrapper event, int position) {
        final MusicManager musicManager = getMusicManager(event);
        if (musicManager.getTrackScheduler().getTrackQueue().isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        musicManager.getTrackScheduler().skipToPosition(position);
        final AudioTrack currentPlaying = musicManager.getAudioPlayer().getPlayingTrack();
        JDALog.info(log, event, "'%s' tracks in queue was skipped and started playing track: '%s'", position,
            currentPlaying.getInfo().title);
        return currentPlaying;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public MemberRemovedTracksInfo removeTracksFromMember(CommandEventWrapper event, String memberId) {
        final Member memberRemoveTracks = Utilities.checkIfMemberInGuildExist(event, memberId, config);
        final MusicManager musicManager = getMusicManager(event);

        if (!musicManager.getTrackScheduler().checkIfMemberAddAnyTracksToQueue(memberRemoveTracks)) {
            throw new UserNotAddedTracksToQueueException(config, event);
        }
        final List<ExtendedAudioTrackInfo> removedTracks = musicManager.getTrackScheduler()
            .removeAllTracksFromMember(memberRemoveTracks);
        return new MemberRemovedTracksInfo(memberRemoveTracks, removedTracks);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MusicManager checkPermissions(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioTrack playingTrack = musicManager.getAudioPlayer().getPlayingTrack();
        if (Objects.isNull(playingTrack)) {
            throw new TrackIsNotPlayingException(config, event);
        }
        if (invokerIsNotTrackSenderOrAdmin(playingTrack, event)) {
            throw new InvokerIsNotTrackSenderOrAdminException(config, event);
        }
        return musicManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean invokerIsNotTrackSenderOrAdmin(AudioTrack track, CommandEventWrapper event) {
        final User dataSender = ((Member) track.getUserData()).getUser();
        final Member messageSender = event.getGuild().getMember(event.getAuthor());
        if (Objects.isNull(messageSender)) return true;

        final ValidateUserDetails details =  Utilities.validateUserDetails(event, config);
        return !(dataSender.getAsTag().equals(event.getAuthor().getAsTag()) || !details.isNotOwner()
            || !details.isNotManager() || !details.isNotDj());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MusicManager getMusicManager(CommandEventWrapper event) {
        return musicManagers.computeIfAbsent(event.getGuild().getIdLong(), guildId -> {
            final MusicManager musicManager = new MusicManager(this, builder, config, event.getGuild(), event);
            event.getGuild().getAudioManager().setSendingHandler(musicManager.getAudioPlayerSendHandler());
            return musicManager;
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MusicManager getMusicManager(Guild guild) {
        return musicManagers.get(guild.getIdLong());
    }

    public ExtendedAudioTrackInfo getCurrentPlayingTrack(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        if (Objects.isNull(musicManager.getAudioPlayer().getPlayingTrack())) return null;
        return new ExtendedAudioTrackInfo(musicManager.getAudioPlayer().getPlayingTrack());
    }
}
