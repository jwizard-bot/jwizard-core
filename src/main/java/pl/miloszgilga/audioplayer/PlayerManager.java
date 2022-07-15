/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: PlayerManager.java
 * Last modified: 12/07/2022, 00:14
 * Project name: franek-bot
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
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlayerManager {

    private static PlayerManager playerManager;
    private final Map<Long, MusicManager> musicManagerMap = new HashMap<>();
    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    private PlayerManager() {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public MusicManager getMusicManager(Guild guild) {
        return musicManagerMap.computeIfAbsent(guild.getIdLong(), guildId -> {
            final MusicManager musicManager = new MusicManager(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
            return musicManager;
        });
    }

    public void loadAndPlay(TextChannel textChannel, String trackURL) {
        final MusicManager musicManager = getMusicManager(textChannel.getGuild());
        audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.getScheduler().queue(audioTrack);
                textChannel.sendMessage("Adding to queue new track").append(audioTrack.getInfo().title).queue();
            }
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> trackList = audioPlaylist.getTracks();
                if (!trackList.isEmpty()) {
                    musicManager.getScheduler().queue(trackList.get(0));
                    textChannel.sendMessage("Adding to queue").append(trackList.get(0).getInfo().title).queue();
                }
            }
            @Override
            public void noMatches() {
                textChannel.sendMessage("Nie znaleziono piosenki").queue();
            }
            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public static PlayerManager getSingletonInstance() {
        if (playerManager == null) {
            playerManager = new PlayerManager();
        }
        return playerManager;
    }
}