/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MusicManager.java
 * Last modified: 17/05/2023, 01:42
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

import net.dv8tion.jda.api.entities.Guild;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import java.util.Queue;

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
public class MusicManager {

    private final RemotePropertyHandler handler;

    @Getter(value = AccessLevel.PUBLIC)     private final AudioPlayer audioPlayer;
    @Getter(value = AccessLevel.PUBLIC)     private final TrackScheduler trackScheduler;
    @Getter(value = AccessLevel.PUBLIC)     private final AudioPlayerSendHandler audioPlayerSendHandler;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MusicManager(
        PlayerManager playerManager, EmbedMessageBuilder builder, BotConfiguration config, Guild guild,
        CommandEventWrapper eventWrapper, RemotePropertyHandler handler
    ) {
        this.handler = handler;
        this.audioPlayer = playerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(config, builder, audioPlayer, eventWrapper, handler);
        this.audioPlayer.setVolume(handler.getPossibleRemoteProperty(RemoteProperty.R_DEFAULT_PLAYER_VOLUME_UNITS,
            guild, Short.class));
        this.audioPlayer.addListener(trackScheduler);
        this.audioPlayerSendHandler = new AudioPlayerSendHandler(audioPlayer, guild);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public SchedulerActions getActions() {
        return trackScheduler.getActions();
    }

    public Queue<AudioQueueExtendedInfo> getQueue() {
        return trackScheduler.getActions().getTrackQueue();
    }

    public boolean isInfinitePlaylistActive() {
        return trackScheduler.getActions().isInfinitePlaylistRepeating();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public short getPlayerVolume() {
        return (short) audioPlayer.getVolume();
    }

    public short resetPlayerVolume(CommandEventWrapper eventWrapper) {
        final short defVolume = handler.getPossibleRemoteProperty(RemoteProperty.R_DEFAULT_PLAYER_VOLUME_UNITS,
            eventWrapper.getGuild(), Short.class);
        audioPlayer.setVolume(defVolume);
        JDALog.info(log, eventWrapper, "Audio player volume was reset to default value (%s points)", defVolume);
        return defVolume;
    }
}
