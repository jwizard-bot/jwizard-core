/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: TrackScheduler.java
 * Last modified: 04/03/2023, 23:05
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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class TrackScheduler extends AudioEventAdapter {

    private final BotConfiguration config;

    private AudioTrack pausedTrack;
    private boolean repeating = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TrackScheduler(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPlayerPause(AudioPlayer player) {

    }

    @Override
    public void onPlayerResume(AudioPlayer player) {

    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {

    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AudioTrack getPausedTrack() {
        return pausedTrack;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
}
