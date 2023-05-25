/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MoveTrackCmd.java
 * Last modified: 16/05/2023, 18:43
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

package pl.miloszgilga.command.dj;

import net.dv8tion.jda.api.entities.MessageEmbed;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.TrackPosition;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractDjCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class MoveTrackCmd extends AbstractDjCommand {

    MoveTrackCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.MOVE_TRACK, config, playerManager, embedBuilder, handler);
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteDjCommand(CommandEventWrapper event) {
        final Integer previousPosition = event.getArgumentAndParse(BotCommandArgument.MOVE_TRACK_POSITION_FROM);
        final Integer requestedPosition = event.getArgumentAndParse(BotCommandArgument.MOVE_TRACK_POSITION_TO);

        final TrackPosition trackPositions = new TrackPosition(previousPosition, requestedPosition);
        final AudioTrack movedTrack = playerManager.moveTrackToSelectedPosition(event, trackPositions);

        final MessageEmbed messageEmbed = embedBuilder
            .createMessage(ResLocaleSet.MOVE_TRACK_POS_TO_SELECTED_LOCATION_MESS, Map.of(
                "movedTrack", Utilities.getRichTrackTitle(movedTrack.getInfo()),
                "previousPosition", String.valueOf(previousPosition),
                "requestedPosition", String.valueOf(requestedPosition)
            ), event.getGuild());
        event.sendEmbedMessage(messageEmbed);
    }
}
