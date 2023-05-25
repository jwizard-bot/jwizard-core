/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JoinToChannelCmd.java
 * Last modified: 16/05/2023, 18:53
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
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractDjCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class JoinToChannelCmd extends AbstractDjCommand {

    JoinToChannelCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.JOIN_TO_CHANNEL, config, playerManager, embedBuilder, handler);
        super.inPlayingMode = true;
        super.allowAlsoForNormal = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteDjCommand(CommandEventWrapper event) {
        final VoiceChannel movedToVoiceChannel = playerManager.moveToMemberCurrentVoiceChannel(event);
        final MessageEmbed messageEmbed = embedBuilder
            .createMessage(ResLocaleSet.MOVE_BOT_TOO_SELECTED_CHANNEL_MESS, Map.of(
                "movedChannel", movedToVoiceChannel.getName()
            ), event.getGuild());
        event.sendEmbedMessage(messageEmbed);
    }
}
