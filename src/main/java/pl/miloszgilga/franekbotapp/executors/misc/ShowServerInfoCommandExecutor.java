/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ShowServerInfoCommandExecutor.java
 * Last modified: 17/07/2022, 18:28
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

package pl.miloszgilga.franekbotapp.executors.misc;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import jdk.jfr.Description;
import pl.miloszgilga.franekbotapp.BotCommand;
import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.messages.MessageEmbedField;

import java.util.List;
import java.util.stream.Collectors;

import static pl.miloszgilga.franekbotapp.BotCommand.HELP;
import static pl.miloszgilga.franekbotapp.BotCommand.HELP_ME;
import static pl.miloszgilga.franekbotapp.ConfigurationLoader.config;


public class ShowServerInfoCommandExecutor extends Command {

    public ShowServerInfoCommandExecutor() {
        name = HELP.getCommandName();
        help = HELP.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]help>")
    protected void execute(CommandEvent event) {
        List<MessageEmbedField> allCommandFields = BotCommand.getAllCommandsAsEnumValues().stream()
                .map(command -> {
                    final String prefixName = String.format("`%s%s`", config.getDefPrefix(), command.getCommandName());
                    return new MessageEmbedField(prefixName, command.getCommandDescription(), false);
                })
                .collect(Collectors.toList());

        final var embedMessage = new EmbedMessage("", String.format(
                "Wielofunkcyjny bot muzyczny + w przyszłości dodatkowe funkcje. Napisany w całości w JAVIE " +
                "przy użyciu wrappera JDA, Lavaplayer oraz biblioteki JacksonJSON. Poniżej znajdziesz listę " +
                "wszystkich dostępnych komend. Listę taką możesz również przywołać w wiadomości prywatnej" +
                "wykorzystując komendę `%s%s`.", config.getDefPrefix(), HELP_ME.getCommandName()),
                EmbedMessageColor.ORANGE,
                allCommandFields
        );
        embedMessage.getBuilder().setTitle(
                String.format("FRANEK BOT v%s", config.getBotVersion()),
                "https://github.com/Milosz08/JDA_Discord_Bot");
        embedMessage.getBuilder().setFooter(String.format("Użytkownik: %s, serwer: %s",
                event.getAuthor().getAsTag(), event.getGuild().getName()), event.getAuthor().getAvatarUrl());
        event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
    }
}