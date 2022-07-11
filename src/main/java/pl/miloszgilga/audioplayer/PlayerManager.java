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

    private static PlayerManager PLAYER_MANAGER;
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
                musicManager.scheduler.queue(audioTrack);
                textChannel.sendMessage("Adding to queue new track").append(audioTrack.getInfo().title).queue();
            }
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> trackList = audioPlaylist.getTracks();
                if (!trackList.isEmpty()) {
                    musicManager.scheduler.queue(trackList.get(0));
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
        if (PLAYER_MANAGER == null) {
            PLAYER_MANAGER = new PlayerManager();
        }
        return PLAYER_MANAGER;
    }
}