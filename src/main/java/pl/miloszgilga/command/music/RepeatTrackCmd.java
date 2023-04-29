/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RepeatTrackCmd.java
 * Last modified: 19/03/2023, 20:44
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
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import static pl.miloszgilga.exception.AudioPlayerException.TrackRepeatsOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class RepeatTrackCmd extends AbstractMusicCommand {

    RepeatTrackCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.REPEAT_TRACK, config, playerManager, embedBuilder, handler);
        super.inPlayingMode = true;
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final int repeats = event.getArgumentAndParse(BotCommandArgument.COUNT_OF_REPEATS);
        if (repeats < 1 || repeats > config.getProperty(BotProperty.J_MAX_REPEATS_SINGLE_TRACK, Integer.class)) {
            throw new TrackRepeatsOutOfBoundsException(config, event);
        }
        playerManager.repeatCurrentTrack(event, repeats);

        final AudioTrackInfo trackInfo = playerManager.getCurrentPlayingTrack(event);
        final MessageEmbed messageEmbed = embedBuilder
            .createMessage(ResLocaleSet.SET_MULTIPLE_REPEATING_TRACK_MESS, Map.of(
                "track", Utilities.getRichTrackTitle(trackInfo),
                "times", repeats,
                "clearRepeatingCmd", BotCommand.CLEAR_REPEAT_TRACK.parseWithPrefix(config)
            ));
        event.appendEmbedMessage(messageEmbed);
    }
}
