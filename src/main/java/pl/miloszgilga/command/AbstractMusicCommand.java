/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioCommand.java
 * Last modified: 05/03/2023, 00:24
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

package pl.miloszgilga.command;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.exception.AudioPlayerException;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractCommand;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.AudioPlayerSendHandler;

import static pl.miloszgilga.exception.CommandException.UsedCommandOnForbiddenChannelException;
import static pl.miloszgilga.exception.AudioPlayerException.ActiveMusicPlayingNotFoundException;
import static pl.miloszgilga.exception.AudioPlayerException.UserOnVoiceChannelNotFoundException;
import static pl.miloszgilga.exception.AudioPlayerException.LockCommandOnTemporaryHaltedException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractMusicCommand extends AbstractCommand {

    protected final BotConfiguration config;
    protected final PlayerManager playerManager;

    protected boolean inPlayingMode;
    protected boolean inListeningMode;
    protected boolean selfJoinable;
    protected boolean isPaused;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected AbstractMusicCommand(
        BotCommand command, BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder
    ) {
        super(command, config, embedBuilder);
        this.config = config;
        this.playerManager = playerManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteCommand(CommandEventWrapper event) {
        final var audioSendHandler = (AudioPlayerSendHandler) event.guild().getAudioManager().getSendingHandler();
        try {
            final GuildVoiceState guildVoiceState = event.guild().getSelfMember().getVoiceState();
            if (Objects.isNull(guildVoiceState) || guildVoiceState.isMuted()) {
                throw new LockCommandOnTemporaryHaltedException(config, event);
            }
            final MusicManager musicManager = playerManager.getMusicManager(event);
            if (inPlayingMode && (Objects.isNull(audioSendHandler) || !audioSendHandler.isInPlayingMode()
                || musicManager.getAudioPlayer().isPaused()) && !isPaused) {
                throw new ActiveMusicPlayingNotFoundException(config, event);
            }
            final AudioTrack pausedTrack = musicManager.getTrackScheduler().getPausedTrack();
            if (isPaused && Objects.isNull(pausedTrack)) {
                throw new AudioPlayerException.TrackIsNotPausedException(config, event);
            }
            if (inListeningMode) {
                final GuildVoiceState userState = event.getMember().getVoiceState();
                if (Objects.isNull(userState) || !userState.inVoiceChannel() || userState.isDeafened()) {
                    throw new UserOnVoiceChannelNotFoundException(config, new EventWrapper(event));
                }
                final VoiceChannel afkChannel = event.getGuild().getAfkChannel();
                if (!Objects.isNull(afkChannel) && Objects.equals(afkChannel, userState.getChannel())) {
                    throw new UsedCommandOnForbiddenChannelException(config, new EventWrapper(event));
                }
                final GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
                if (!Objects.isNull(voiceState) && !voiceState.inVoiceChannel()) {
                    if (selfJoinable) event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
                }
            }
            doExecuteMusicCommand(event);
        } catch (BotException ex) {
            event.textChannel()
                .sendMessageEmbeds(embedBuilder.createErrorMessage(event, ex))
                .queue();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteMusicCommand(CommandEventWrapper event);
}
