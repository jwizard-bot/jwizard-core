/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: IllegalCommandArgumentsException.java
 * Last modified: 11/07/2022, 21:39
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

package pl.miloszgilga.exceptions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.messages.EmbedMessageColor;
import pl.miloszgilga.messages.MessageEmbedField;


public class IllegalCommandArgumentsException extends JdaIllegalChatStateException {

    public IllegalCommandArgumentsException(MessageReceivedEvent event, String commandSyntax) {
        super(event);
        final var embedMessage = new EmbedMessage("ERROR!", "Nieprawidłowe argumenty komendy", EmbedMessageColor.RED, List.of(
                new MessageEmbedField("Komendy należy używać zgodne ze składnią: ", commandSyntax, false)
        ));
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return "Błędne argumenty komendy" + getEvent().getAuthor();
    }
}