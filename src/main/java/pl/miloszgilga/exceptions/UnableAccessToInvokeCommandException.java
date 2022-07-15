/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UnableAccessToInvokeCommandException.java
 * Last modified: 14/07/2022, 22:02
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

import com.jagrosh.jdautilities.command.CommandEvent;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.messages.EmbedMessageColor;


public class UnableAccessToInvokeCommandException extends JdaIllegalChatStateException {

    public UnableAccessToInvokeCommandException(CommandEvent event, String additionalMessage) {
        super(event);
        final var embedMessage = new EmbedMessage("BRAK DOSTĘPU!", "Nie masz odpowiednich praw, aby użyć tej " +
                "komendy. Aby użyć tej komendy niezbędne jest posiadanie " + additionalMessage, EmbedMessageColor.RED);
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    public UnableAccessToInvokeCommandException(CommandEvent event) {
        super(event);
        final var embedMessage = new EmbedMessage("BRAK DOSTĘPU!", "Nie masz odpowiednich praw, aby użyć tej komendy.",
                EmbedMessageColor.RED);
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return "Nieautoryzowany dostęp" + getEvent().getAuthor();
    }
}