/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlayerManager.java
 * Last modified: 17/05/2023, 01:49
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

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

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
import pl.miloszgilga.dto.TrackPosition;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.MemberRemovedTracksInfo;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.CommandException.UserIsAlreadyWithBotException;
import static pl.miloszgilga.exception.AudioPlayerException.TrackIsNotPlayingException;
import static pl.miloszgilga.exception.AudioPlayerException.TrackQueueIsEmptyException;
import static pl.miloszgilga.exception.AudioPlayerException.TrackPositionsIsTheSameException;
import static pl.miloszgilga.exception.AudioPlayerException.TrackPositionOutOfBoundsException;
import static pl.miloszgilga.exception.AudioPlayerException.UserNotAddedTracksToQueueException;
import static pl.miloszgilga.exception.AudioPlayerException.UserOnVoiceChannelNotFoundException;
import static pl.miloszgilga.exception.AudioPlayerException.InvokerIsNotTrackSenderOrAdminException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class PlayerManager extends DefaultAudioPlayerManager implements IPlayerManager {

    private static final int CONNECTION_TIMEOUT = 10000;

    private final BotConfiguration config;
    private final EmbedMessageBuilder builder;
    private final RemotePropertyHandler handler;
    private final Map<Long, MusicManager> musicManagers = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    PlayerManager(BotConfiguration config, EmbedMessageBuilder builder, RemotePropertyHandler handler) {
        this.config = config;
        this.builder = builder;
        this.handler = handler;
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
        final AudioLoadResultHandler audioLoadResultHandler = new AudioLoaderResultImpl(musicManager, config, handler,
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
        final AudioTrack pausedTrack = musicManager.getActions().getPausedTrack();
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
        if (musicManager.getQueue().isEmpty()) {
            musicManager.getAudioPlayer().stopTrack();
        } else {
            musicManager.getActions().nextTrack();
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
        musicManager.getActions().setCountOfRepeats(countOfRepeats);
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
        musicManager.getActions().setInfiniteRepeating(!musicManager.getActions().isInfiniteRepeating());
        final boolean isRepeating = musicManager.getActions().isInfiniteRepeating();
        final String currentTrackTitle = getCurrentPlayingTrack(event).title;
        if (isRepeating) {
            JDALog.info(log, event, "Current playing track '%s' has been placed in infinite loop", currentTrackTitle);
        } else {
            JDALog.info(log, event, "Current playing track '%s' has been removed from infinite loop", currentTrackTitle);
        }
        return musicManager.getActions().isInfiniteRepeating();
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
        if (musicManager.getQueue().isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        musicManager.getActions().skipToPosition(position);
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

        if (!musicManager.getActions().checkIfMemberAddAnyTracksToQueue(memberRemoveTracks)) {
            throw new UserNotAddedTracksToQueueException(config, event);
        }
        final List<ExtendedAudioTrackInfo> removedTracks = musicManager.getActions()
            .removeAllTracksFromMember(memberRemoveTracks);

        JDALog.info(log, event, "Following tracks was removed '%s', added by member: '%s'", removedTracks,
            memberRemoveTracks.getUser().getAsTag());
        return new MemberRemovedTracksInfo(memberRemoveTracks, removedTracks);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean toggleInfinitePlaylistLoop(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        if (musicManager.getQueue().isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        final boolean isTurnOn = musicManager.getActions().toggleInfinitePlaylistRepeating();
        JDALog.info(log, event, "Current playlist was turn '%s' for infinite repeating", isTurnOn ? "ON" : "OFF");
        return isTurnOn;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public VoiceChannel moveToMemberCurrentVoiceChannel(CommandEventWrapper event) {
        final VoiceChannel voiceChannelWithMember = event.getGuild().getVoiceChannels().stream()
            .filter(c -> c.getMembers().contains(event.getMember()))
            .findFirst()
            .orElseThrow(() -> new UserOnVoiceChannelNotFoundException(config, event));

        final Member botMember = event.getGuild().getSelfMember();
        if (voiceChannelWithMember.getMembers().contains(botMember)) {
            throw new UserIsAlreadyWithBotException(config, event);
        }
        event.getGuild().moveVoiceMember(botMember, voiceChannelWithMember).complete();
        JDALog.info(log, event, "Bot was successfully moved to channel '%s'", voiceChannelWithMember.getName());
        return voiceChannelWithMember;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public AudioTrack moveTrackToSelectedPosition(CommandEventWrapper event, TrackPosition position) {
        final MusicManager musicManager = getMusicManager(event);
        final SchedulerActions actions = musicManager.getActions();
        if (musicManager.getQueue().isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        if (actions.checkInvTrackPosition(position.previous()) || actions.checkInvTrackPosition(position.selected())) {
            throw new TrackPositionOutOfBoundsException(config, event, musicManager.getQueue().size());
        }
        if (position.previous() == position.selected()) {
            throw new TrackPositionsIsTheSameException(config, event);
        }
        final AudioTrack movedAudioTrack = musicManager.getActions().moveToPosition(position);
        JDALog.info(log, event, "Audio Track '%s' was successfully moved from '%d' to '%d' position in queue",
            movedAudioTrack.getInfo().title, position.previous(), position.selected());
        return movedAudioTrack;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int clearQueue(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        final int countOfTracksInQueue = musicManager.getQueue().size();
        musicManager.getActions().clearQueue();
        JDALog.info(log, event, "Queue was cleared. Removed '%d' audio tracks from queue.", countOfTracksInQueue);
        return countOfTracksInQueue;
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

        final ValidateUserDetails details = Utilities.validateUserDetails(event, handler);
        return !(dataSender.getAsTag().equals(event.getAuthor().getAsTag()) || !details.isNotOwner()
            || !details.isNotManager() || !details.isNotDj());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MusicManager getMusicManager(CommandEventWrapper event) {
        return musicManagers.computeIfAbsent(event.getGuild().getIdLong(), guildId -> {
            final MusicManager musicManager = new MusicManager(this, builder, config, event.getGuild(), event, handler);
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
