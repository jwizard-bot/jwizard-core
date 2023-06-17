/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RepeatTrackCmd.java
 * Last modified: 16/05/2023, 19:57
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
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import static pl.miloszgilga.exception.AudioPlayerException.TrackRepeatsOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class RepeatTrackCmd extends AbstractMusicCommand {

    RepeatTrackCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler, CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.REPEAT_TRACK, config, playerManager, embedBuilder, handler, cacheableCommandStateDao);
        super.inPlayingMode = true;
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final Integer repeats = event.getArgumentAndParse(BotCommandArgument.COUNT_OF_REPEATS);
        final Integer maxRepeats = handler.getPossibleRemoteProperty(RemoteProperty.R_MAX_REPEATS_SINGLE_TRACK,
            event.getGuild(), Integer.class);
        if (repeats < 1 || repeats > maxRepeats) {
            throw new TrackRepeatsOutOfBoundsException(config, handler, event);
        }
        playerManager.repeatCurrentTrack(event, repeats);

        final AudioTrackInfo trackInfo = playerManager.getCurrentPlayingTrack(event);
        final MessageEmbed messageEmbed = embedBuilder
            .createMessage(ResLocaleSet.SET_MULTIPLE_REPEATING_TRACK_MESS, Map.of(
                "track", Utilities.getRichTrackTitle(trackInfo),
                "times", repeats,
                "clearRepeatingCmd", BotCommand.CLEAR_REPEAT_TRACK.parseWithPrefix(config)
            ), event.getGuild());
        event.appendEmbedMessage(messageEmbed);
    }
}
