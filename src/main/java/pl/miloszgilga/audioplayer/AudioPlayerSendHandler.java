/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioPlayerSendHandler.java
 * Last modified: 19/03/2023, 14:07
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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import java.util.Objects;
import java.nio.ByteBuffer;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class AudioPlayerSendHandler implements AudioSendHandler {

    private final Guild guild;
    private final AudioPlayer audioPlayer;
    private AudioFrame audioFrame;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    AudioPlayerSendHandler(AudioPlayer audioPlayer, Guild guild) {
        this.audioPlayer = audioPlayer;
        this.guild = guild;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isInPlayingMode() {
        boolean isActive = Objects.requireNonNull(guild.getSelfMember().getVoiceState()).inVoiceChannel();
        return !Objects.isNull(audioPlayer.getPlayingTrack()) && isActive;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean canProvide() {
        audioFrame = audioPlayer.provide();
        return audioFrame != null;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(audioFrame.getData());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isOpus() {
        return true;
    }
}
