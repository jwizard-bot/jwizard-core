/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CurrentPlayingCmd.java
 * Last modified: 28/04/2023, 23:36
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

package pl.miloszgilga.command.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.CurrentPlayEmbedContent;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.ExtendedAudioTrackInfo;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class CurrentPlayingCmd extends AbstractMusicCommand {

    CurrentPlayingCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.CURRENT_PLAYING, config, playerManager, embedBuilder, handler);
        super.inPlayingMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final MusicManager musicManager = playerManager.getMusicManager(event);
        final ExtendedAudioTrackInfo track = playerManager.getCurrentPlayingTrack(event);

        final String trackTimestamp = Utilities.convertMilisToDate(track.getTimestamp());
        final String trackMaxDuration = Utilities.convertMilisToDate(track.getMaxDuration());

        final CurrentPlayEmbedContent content = new CurrentPlayEmbedContent(
            ResLocaleSet.CURRENT_PLAYING_TRACK_MESS,
            ResLocaleSet.CURRENT_PLAYING_TIMESTAMP_MESS,
            Utilities.getRichTrackTitle(track),
            track.getThumbnailUrl(),
            ((Member) musicManager.getAudioPlayer().getPlayingTrack().getUserData()).getUser().getAsTag(),
            String.format("%s / %s", trackTimestamp, trackMaxDuration),
            Utilities.convertMilisToDate(track.getMaxDuration() - track.getTimestamp()),
            Utilities.createPlayerPercentageTrack(track.getTimestamp(), track.getMaxDuration())
        );
        final MessageEmbed messageEmbed = embedBuilder.createCurrentPlayingMessage(event, content);
        event.appendEmbedMessage(messageEmbed);
    }
}
