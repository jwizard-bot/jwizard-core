/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractMusicCommand.java
 * Last modified: 19/03/2023, 21:43
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

package pl.miloszgilga.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
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
import static pl.miloszgilga.exception.AudioPlayerException.UserOnVoiceChannelWithBotNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractMusicCommand extends AbstractCommand {

    protected final BotConfiguration config;
    protected final PlayerManager playerManager;

    protected boolean inPlayingMode;
    protected boolean inIdleMode;
    protected boolean onSameChannelWithBot;
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
        final var audioSendHandler = (AudioPlayerSendHandler) event.getGuild().getAudioManager().getSendingHandler();

        final GuildVoiceState guildVoiceState = event.getGuild().getSelfMember().getVoiceState();
        if (Objects.isNull(guildVoiceState) || guildVoiceState.isMuted()) {
            throw new LockCommandOnTemporaryHaltedException(config, event);
        }
        final MusicManager musicManager = playerManager.getMusicManager(event);
        if (inPlayingMode && (Objects.isNull(audioSendHandler) || !audioSendHandler.isInPlayingMode()
            || musicManager.getAudioPlayer().isPaused()) && !isPaused) {
            throw new ActiveMusicPlayingNotFoundException(config, event);
        }
        if (!inIdleMode) {
            final AudioTrack pausedTrack = musicManager.getTrackScheduler().getPausedTrack();
            if (isPaused && Objects.isNull(pausedTrack)) {
                throw new AudioPlayerException.TrackIsNotPausedException(config, event);
            }
            final GuildVoiceState userState = event.getMember().getVoiceState();
            final GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
            if (Objects.isNull(userState) || !userState.inVoiceChannel() || userState.isDeafened()) {
                throw new UserOnVoiceChannelNotFoundException(config, event);
            }
            final VoiceChannel afkChannel = event.getGuild().getAfkChannel();
            if (!Objects.isNull(afkChannel) && Objects.equals(afkChannel, userState.getChannel())) {
                throw new UsedCommandOnForbiddenChannelException(config, event);
            }
            if (selfJoinable && !voiceState.inVoiceChannel()) {
                event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
            } else {
                final boolean isNotOwner = !event.getAuthor().getId().equals(event.getGuild().getOwnerId());
                final boolean isNotManager = !event.getMember().hasPermission(Permission.MANAGE_SERVER);
                if (!Objects.equals(voiceState.getChannel(), userState.getChannel()) && onSameChannelWithBot
                    && (isNotOwner || isNotManager)) {
                    throw new UserOnVoiceChannelWithBotNotFoundException(config, event);
                }
            }
        }
        doExecuteMusicCommand(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteMusicCommand(CommandEventWrapper event);
}
