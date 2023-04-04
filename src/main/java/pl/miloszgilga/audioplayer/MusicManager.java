/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MusicManager.java
 * Last modified: 19/03/2023, 23:17
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

import net.dv8tion.jda.api.entities.Guild;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import java.util.Queue;

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
public class MusicManager {

    private final BotConfiguration config;

    @Getter(value = AccessLevel.PUBLIC)     private final AudioPlayer audioPlayer;
    @Getter(value = AccessLevel.PUBLIC)     private final TrackScheduler trackScheduler;
    @Getter(value = AccessLevel.PUBLIC)     private final AudioPlayerSendHandler audioPlayerSendHandler;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MusicManager(
        PlayerManager playerManager, EmbedMessageBuilder builder, BotConfiguration config, Guild guild,
        CommandEventWrapper eventWrapper
    ) {
        this.config = config;
        this.audioPlayer = playerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(config, builder, audioPlayer, eventWrapper);
        this.audioPlayer.setVolume(config.getProperty(BotProperty.J_DEFAULT_PLAYER_VOLUME_UNITS, Short.class));
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
        final short defVolume = config.getProperty(BotProperty.J_DEFAULT_PLAYER_VOLUME_UNITS, Short.class);
        audioPlayer.setVolume(defVolume);
        JDALog.info(log, eventWrapper, "Audio player volume was reset to default value (%s points)", defVolume);
        return defVolume;
    }
}
