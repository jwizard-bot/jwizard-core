/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlayTrackCmd.java
 * Last modified: 23/03/2023, 18:41
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

import org.apache.commons.validator.routines.UrlValidator;

import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class PlayTrackCmd extends AbstractMusicCommand {

    PlayTrackCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.PLAY_TRACK, config, playerManager, embedBuilder);
        super.inSameChannelWithBot = true;
        super.selfJoinable = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final UrlValidator urlValidator = new UrlValidator();
        String searchPhrase = event.getArgs().get(0);
        boolean urlPatternValid = urlValidator.isValid(searchPhrase);
        if (urlPatternValid) {
            searchPhrase = searchPhrase.replaceAll(" ", "");
        } else {
            searchPhrase = "ytsearch: " + searchPhrase + " audio";
        }
        final MusicManager musicManager = playerManager.getMusicManager(event);
        if (!Objects.isNull(musicManager)) {
            musicManager.getTrackScheduler().setDeliveryEvent(event);
        }
        playerManager.loadAndPlay(event, searchPhrase, urlPatternValid);
    }
}
