/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MusicBotNotActiveException.java
 * Last modified: 14/07/2022, 22:38
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


public final class MusicBotNotActiveException extends JdaIllegalChatStateException {

    public MusicBotNotActiveException(CommandEvent event) {
        super(event);
        final var embedMessage = new EmbedMessage("UWAGA!", "Aby była możliwość przeniesienia bota na inny kanał, bot " +
                "musi znajdować się na kanale głosowym.", EmbedMessageColor.ORANGE);
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return String.format("Próba przeniesienia bota muzycznego na inny kanał, gdy ten jest nieaktywny przez '%s'",
                getEvent().getAuthor().getAsTag());
    }
}
