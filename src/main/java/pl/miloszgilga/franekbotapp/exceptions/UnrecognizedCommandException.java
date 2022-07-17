/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UnrecognizedCommandException.java
 * Last modified: 12/07/2022, 00:19
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

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.messages.MessageEmbedField;

import static pl.miloszgilga.franekbotapp.Command.HELP;
import static pl.miloszgilga.franekbotapp.Command.HELP_ME;
import static pl.miloszgilga.franekbotapp.FranekBot.config;


public class UnrecognizedCommandException extends RuntimeException {

    private final MessageReceivedEvent event;

    public UnrecognizedCommandException(MessageReceivedEvent event) {
        this.event = event;
        final var embedMessage = new EmbedMessage("ERROR!", "Nieznana komenda", EmbedMessageColor.RED, List.of(
                new MessageEmbedField("Komendy należy używać zgodne ze składnią: ",
                        String.format("`%s<nazwa komendy> [...argumenty]`", config.getDefPrefix()), false),
                new MessageEmbedField("Aby uzyskać pełną listę komend wpisz: ",
                        String.format("`%s%s` lub `%s%s`", config.getDefPrefix(), HELP.getCommandName(),
                                config.getDefPrefix(), HELP_ME.getCommandName()), false)
        ));
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }

    @Override
    public String getMessage() {
        return String.format("Próba odwołania się do nieistniejącej komendy przez '%s'", event.getAuthor().getAsTag());
    }
}