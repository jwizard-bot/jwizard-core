/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ExtendedAudioTrackInfo.java
 * Last modified: 28/03/2023, 23:49
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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
public class ExtendedAudioTrackInfo extends AudioTrackInfo {

    private String thumbnailUrl;
    private final AudioTrack audioTrack;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ExtendedAudioTrackInfo(AudioTrack track) {
        super(track.getInfo().title, track.getInfo().author, track.getInfo().length, track.getInfo().identifier,
            track.getInfo().isStream, track.getInfo().uri);
        this.audioTrack = track;
        setYoutubeThumbnail();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setYoutubeThumbnail() {
        if (!(audioTrack instanceof YoutubeAudioTrack)) return;
        thumbnailUrl = "https://img.youtube.com/vi/" + audioTrack.getIdentifier() + "/0.jpg";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public long getApproxTime() {
        return audioTrack.getDuration() - audioTrack.getPosition();
    }

    public long getTimestamp() {
        return audioTrack.getPosition();
    }

    public long getMaxDuration() {
        return audioTrack.getDuration();
    }
}
