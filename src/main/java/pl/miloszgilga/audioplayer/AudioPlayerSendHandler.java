/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioPlayerSendHandler.java
 * Last modified: 02/04/2023, 17:31
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
        return !Objects.isNull(audioFrame);
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
