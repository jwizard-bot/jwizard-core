/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: InfiniteLoopTrackCmd.java
 * Last modified: 19/03/2023, 14:56
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
 * The software is provided “as is”, without warranty of any kind, express or implied, including but not limited
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
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.TrackScheduler;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class InfiniteLoopTrackCmd extends AbstractMusicCommand {

    InfiniteLoopTrackCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.LOOP_TRACK, config, playerManager, embedBuilder);
        super.inPlayingMode = true;
        super.inSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final boolean isRepeating = playerManager.toggleInfiniteLoopCurrentTrack(event);
        LocaleSet messsage = LocaleSet.REMOVE_TRACK_FROM_INFINITE_LOOP_MESS;
        if (isRepeating) {
            messsage = LocaleSet.ADD_TRACK_TO_INFINITE_LOOP_MESS;
        }
        final AudioTrackInfo playingTrack = playerManager.getCurrentPlayingTrack(event);
        final MessageEmbed messageEmbed = embedBuilder.createMessage(messsage, Map.of(
            "track", TrackScheduler.getRichTrackTitle(playingTrack),
            "loopCmd", BotCommand.LOOP_TRACK.parseWithPrefix(config)
        ));
        event.appendEmbedMessage(messageEmbed);
    }
}
