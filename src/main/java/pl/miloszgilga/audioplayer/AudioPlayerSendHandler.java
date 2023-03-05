/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioPlayerSendHandler.java
 * Last modified: 04/03/2023, 23:06
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

import net.dv8tion.jda.api.audio.AudioSendHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import java.nio.Buffer;
import java.nio.ByteBuffer;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private final MutableAudioFrame audioFrame = new MutableAudioFrame();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        audioFrame.setBuffer(byteBuffer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean canProvide() {
        return audioPlayer.provide(audioFrame);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ByteBuffer provide20MsAudio() {
        final Buffer buffer = ((Buffer) this.byteBuffer).flip();
        return (ByteBuffer) buffer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isOpus() {
        return true;
    }
}
