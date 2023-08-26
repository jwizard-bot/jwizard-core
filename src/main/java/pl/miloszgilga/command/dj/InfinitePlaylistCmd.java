/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: InfinitePlaylistCmd.java
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

package pl.miloszgilga.command.dj;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractDjCommand;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.IEnumerableLocaleSet;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class InfinitePlaylistCmd extends AbstractDjCommand {

    InfinitePlaylistCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler, CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.INFINITE_PLAYLIST, config, playerManager, embedBuilder, handler, cacheableCommandStateDao);
        super.onSameChannelWithBot = true;
        super.inPlayingMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteDjCommand(CommandEventWrapper event) {
        final boolean isPlaylistRepeating = playerManager.toggleInfinitePlaylistLoop(event);
        IEnumerableLocaleSet messsage = ResLocaleSet.REMOVE_PLAYLIST_FROM_INFINITE_LOOP_MESS;
        if (isPlaylistRepeating) {
            messsage = ResLocaleSet.ADD_PLAYLIST_TO_INFINITE_LOOP_MESS;
        }
        final MessageEmbed messageEmbed = embedBuilder.createMessage(messsage, Map.of(
            "playlistLoopCmd", BotCommand.INFINITE_PLAYLIST.parseWithPrefix(config)
        ), event.getGuild());
        event.appendEmbedMessage(messageEmbed);
    }
}
