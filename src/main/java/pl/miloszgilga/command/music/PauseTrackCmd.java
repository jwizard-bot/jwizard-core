/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PauseTrackCmd.java
 * Last modified: 16/05/2023, 18:48
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

import net.dv8tion.jda.api.entities.MessageEmbed;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.PauseTrackEmbedContent;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class PauseTrackCmd extends AbstractMusicCommand {

    private static final int MAX_VIS_BLOCKS_COUNT = 48; // embed 49 MAX

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    PauseTrackCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.PAUSE_TRACK, config, playerManager, embedBuilder, handler);
        super.inPlayingMode = true;
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        playerManager.pauseCurrentTrack(event);

        final AudioTrack track = playerManager.getMusicManager(event.getGuild()).getAudioPlayer().getPlayingTrack();
        final PauseTrackEmbedContent content = new PauseTrackEmbedContent(
            ResLocaleSet.PAUSE_TRACK_MESS,
            Map.of(
                "track", Utilities.getRichTrackTitle(track.getInfo()),
                "invoker", event.getAuthorTag(),
                "resumeCmd", BotCommand.RESUME_TRACK.parseWithPrefix(config)
            ),
            Utilities.convertMilisToDate(track.getPosition()),
            Utilities.convertMilisToDate(track.getDuration() - track.getPosition()),
            Utilities.convertMilisToDate(track.getDuration()),
            Utilities.createPlayerPercentageTrack(track.getPosition(), track.getDuration(), MAX_VIS_BLOCKS_COUNT)
        );
        final MessageEmbed messageEmbed = embedBuilder.createPauseTrackMessage(content, event.getGuild());
        event.appendEmbedMessage(messageEmbed);
    }
}
