/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: EmptyAudioQueueException.java
 * Last modified: 14/07/2022, 18:39
 * Project name: franek-bot
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

package pl.miloszgilga.franekbotapp.exceptions;

import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.List;

import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.messages.MessageEmbedField;

import static pl.miloszgilga.franekbotapp.FranekBot.config;
import static pl.miloszgilga.franekbotapp.Command.MUSIC_PLAY;


public class EmptyAudioQueueException extends JdaIllegalChatStateException {

    public EmptyAudioQueueException(CommandEvent event) {
        super(event);
        final var embedMessage = new EmbedMessage("UWAGA!", "Kolejka piosenek jest pusta.", EmbedMessageColor.ORANGE,
                List.of(new MessageEmbedField(
                        "Aby dodać nową piosenkę użyj komendy",
                        String.format("`%s%s [link lub nazwa piosenki]`", config.getDefPrefix(), MUSIC_PLAY.getCommandName()),
                        false))
        );
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return "Kolejka piosenek jest pusta" + getEvent().getAuthor();
    }
}