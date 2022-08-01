/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserHasNotStatisticsYetException.java
 * Last modified: 31/07/2022, 21:41
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

import net.dv8tion.jda.api.entities.User;
import com.jagrosh.jdautilities.command.CommandEvent;

import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;


public final class UserHasNotStatisticsYetException extends JdaIllegalChatStateException {

    private final User findingUser;

    public UserHasNotStatisticsYetException(CommandEvent event, User findingUser) {
        super(event);
        this.findingUser = findingUser;
        final var embedMessage = new EmbedMessage("BRAK STATYSTYK!", String.format(
                "Użytkownik **%s** nie posiada jeszcze żadnych statystyk serwera. Komenda dostępna jest tylko " +
                "dla użytkowników aktywnych na serwerze.", findingUser.getAsTag()), EmbedMessageColor.ORANGE
        );
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return String.format("Próba odwołania się do nieistniejących statystyk serwera '%s' użytkownika o id '%s'",
                getEvent().getGuild().getName(), findingUser.getAsTag());
    }
}