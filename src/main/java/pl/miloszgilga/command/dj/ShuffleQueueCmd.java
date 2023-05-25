/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ShuffleQueueCmd.java
 * Last modified: 16/05/2023, 18:50
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

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractDjCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import static pl.miloszgilga.exception.AudioPlayerException.TrackQueueIsEmptyException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class ShuffleQueueCmd extends AbstractDjCommand {

    ShuffleQueueCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.SHUFFLE_QUEUE, config, playerManager, embedBuilder, handler);
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteDjCommand(CommandEventWrapper event) {
        final MusicManager musicManager = playerManager.getMusicManager(event);
        if (musicManager.getQueue().isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        playerManager.shuffleQueue(event);

        final MessageEmbed messageEmbed = embedBuilder.createMessage(ResLocaleSet.QUEUE_WAS_SHUFFLED_MESS, Map.of(
            "showQueueCmd", BotCommand.QUEUE.parseWithPrefix(config)
        ), event.getGuild());
        event.appendEmbedMessage(messageEmbed);
    }
}
