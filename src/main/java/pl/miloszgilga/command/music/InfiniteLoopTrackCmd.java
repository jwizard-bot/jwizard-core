/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: InfiniteLoopTrackCmd.java
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

package pl.miloszgilga.command.music;

import net.dv8tion.jda.api.entities.MessageEmbed;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.IEnumerableLocaleSet;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class InfiniteLoopTrackCmd extends AbstractMusicCommand {

    InfiniteLoopTrackCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler, CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.LOOP_TRACK, config, playerManager, embedBuilder, handler, cacheableCommandStateDao);
        super.inPlayingMode = true;
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final boolean isRepeating = playerManager.toggleInfiniteLoopCurrentTrack(event);
        IEnumerableLocaleSet messsage = ResLocaleSet.REMOVE_TRACK_FROM_INFINITE_LOOP_MESS;
        if (isRepeating) {
            messsage = ResLocaleSet.ADD_TRACK_TO_INFINITE_LOOP_MESS;
        }
        final AudioTrackInfo playingTrack = playerManager.getCurrentPlayingTrack(event);
        final MessageEmbed messageEmbed = embedBuilder.createMessage(messsage, Map.of(
            "track", Utilities.getRichTrackTitle(playingTrack),
            "loopCmd", BotCommand.LOOP_TRACK.parseWithPrefix(config)
        ), event.getGuild());
        event.appendEmbedMessage(messageEmbed);
    }
}
