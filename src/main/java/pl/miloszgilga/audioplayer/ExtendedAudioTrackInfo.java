/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ExtendedAudioTrackInfo.java
 * Last modified: 16/03/2023, 17:15
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

    public long getTimestamp() {
        return audioTrack.getPosition();
    }

    public long getMaxDuration() {
        return audioTrack.getDuration();
    }
}
