/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlayTrackCmd.java
 * Last modified: 05/03/2023, 00:46
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.command.music;

import lombok.extern.slf4j.Slf4j;

import com.jagrosh.jdautilities.command.CommandEvent;
import org.apache.commons.validator.routines.UrlValidator;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class PlayTrackCmd extends AbstractMusicCommand {

    PlayTrackCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.PLAY_TRACK, config, playerManager, embedBuilder);
        super.inPlayingMode = false;
        super.inListeningMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEvent event) {
        final UrlValidator urlValidator = new UrlValidator();
        String searchPhrase = event.getArgs();
        boolean urlPatternValid = urlValidator.isValid(searchPhrase);
        if (urlPatternValid) {
            searchPhrase = searchPhrase.replaceAll(" ", "");
        } else {
            searchPhrase = "ytsearch: " + searchPhrase + " audio";
        }
        playerManager.loadAndPlay(event, searchPhrase, urlPatternValid);
    }
}
