/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: FindingUserByGuidNotFoundException.java
 * Last modified: 31/07/2022, 21:33
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

import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;


public final class FindingUserByGuidNotFoundException extends JdaIllegalChatStateException {

    private final String searchUserId;

    public FindingUserByGuidNotFoundException(CommandEvent event, String searchUserId) {
        super(event);
        this.searchUserId = searchUserId;
        final var embedMessage = new EmbedMessage("NIE ZNALEZIONO UŻYTKOWNIKA!", String.format(
                "Nie odnaleziono użytkownia **%s**. Komenda dostępna jest tylko dla użytkowników " +
                "znajdujących się na obecnym serwerze.", searchUserId), EmbedMessageColor.RED
        );
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return String.format("Próba wyszukania użytkownika na podstawie id '%s' nieznajdującego się na serwerze '%s'",
                searchUserId, getEvent().getGuild().getName());
    }
}