/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractMusicCommand.java
 * Last modified: 15/05/2023, 17:40
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

package pl.miloszgilga.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.exception.AudioPlayerException;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.AbstractCommand;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemoteModuleProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.AudioPlayerSendHandler;

import static pl.miloszgilga.exception.ModuleException.MusicModuleIsTurnedOffException;
import static pl.miloszgilga.exception.CommandException.UsedCommandOnForbiddenChannelException;
import static pl.miloszgilga.exception.AudioPlayerException.ForbiddenTextChannelException;
import static pl.miloszgilga.exception.AudioPlayerException.ActiveMusicPlayingNotFoundException;
import static pl.miloszgilga.exception.AudioPlayerException.UserOnVoiceChannelNotFoundException;
import static pl.miloszgilga.exception.AudioPlayerException.LockCommandOnTemporaryHaltedException;
import static pl.miloszgilga.exception.AudioPlayerException.UserOnVoiceChannelWithBotNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractMusicCommand extends AbstractCommand {

    protected final RemotePropertyHandler handler;
    protected final BotConfiguration config;
    protected final PlayerManager playerManager;

    protected boolean inPlayingMode;
    protected boolean inIdleMode;
    protected boolean onSameChannelWithBot;
    protected boolean selfJoinable;
    protected boolean isPaused;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected AbstractMusicCommand(
        BotCommand command, BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler, CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(command, config, embedBuilder, handler, cacheableCommandStateDao);
        this.config = config;
        this.handler = handler;
        this.playerManager = playerManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteCommand(CommandEventWrapper event) {
        if (!handler.getPossibleRemoteModuleProperty(RemoteModuleProperty.R_MUSIC_MODULE_ENABLED, event.getGuild())) {
            throw new MusicModuleIsTurnedOffException(config, event);
        }
        final var audioSendHandler = (AudioPlayerSendHandler) event.getGuild().getAudioManager().getSendingHandler();

        final String channelId = handler.getPossibleRemoteProperty(RemoteProperty.R_TEXT_MUSIC_CHANNEL_ID, event.getGuild());
        if (!Objects.isNull(channelId) && !event.getTextChannel().getId().equals(channelId)) {
            final TextChannel sendingChannel = event.getGuild().getTextChannelById(channelId);
            final String textChannelName = Objects.isNull(sendingChannel) ? StringUtils.EMPTY : sendingChannel.getName();
            throw new ForbiddenTextChannelException(config, event, textChannelName);
        }
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
            final AudioTrack pausedTrack = musicManager.getActions().getPausedTrack();
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
