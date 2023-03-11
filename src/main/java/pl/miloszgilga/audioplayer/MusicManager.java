/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MusicManager.java
 * Last modified: 04/03/2023, 22:52
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

import net.dv8tion.jda.api.entities.Guild;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class MusicManager {

    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private final AudioPlayerSendHandler audioPlayerSendHandler;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MusicManager(
        PlayerManager playerManager, EmbedMessageBuilder builder, BotConfiguration config, Guild guild,
        EventWrapper eventWrapper
    ) {
        this.audioPlayer = playerManager.createPlayer();
        this.trackScheduler = new TrackScheduler(config, builder, audioPlayer, eventWrapper);
        this.audioPlayer.addListener(trackScheduler);
        this.audioPlayerSendHandler = new AudioPlayerSendHandler(audioPlayer, guild);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AudioPlayerSendHandler getAudioPlayerSendHandler() {
        return audioPlayerSendHandler;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }
}
