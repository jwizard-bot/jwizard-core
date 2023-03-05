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

import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.command.JDAMusicCommand;
import pl.miloszgilga.core.loader.JDACommandLazyService;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.AudioPlayerException.MusicBotIsCurrentlyUsedException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDACommandLazyService
public class PlayTrackCmd extends JDAMusicCommand {

    PlayTrackCmd(BotConfiguration config, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.PLAY_TRACK, config, embedBuilder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void execute(CommandEvent event) {
        try {
            super.execute(event);
            if (Objects.isNull(super.voiceChannel)) {
                throw new MusicBotIsCurrentlyUsedException(super.config, new EventWrapper(event));
            }


        } catch (BotException ex) {
            event.getChannel()
                .sendMessageEmbeds(embedBuilder.createErrorMessage(new EventWrapper(event), ex.getMessage()))
                .queue();
        }
    }
}
