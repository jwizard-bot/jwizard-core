/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetInfinitePlaylistCmd.java
 * Last modified: 02/04/2023, 16:36
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

package pl.miloszgilga.command.dj;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractDjCommand;
import pl.miloszgilga.core.IEnumerableLocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class InfinitePlaylistCmd extends AbstractDjCommand {

    InfinitePlaylistCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.INFINITE_PLAYLIST, config, playerManager, embedBuilder);
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
        ));
        event.appendEmbedMessage(messageEmbed);
    }
}
