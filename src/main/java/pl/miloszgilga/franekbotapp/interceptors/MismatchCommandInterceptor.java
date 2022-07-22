/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MismatchCommandInterceptor.java
 * Last modified: 15/07/2022, 02:35
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

package pl.miloszgilga.franekbotapp.interceptors;

import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.exceptions.UnrecognizedCommandException;

import static pl.miloszgilga.franekbotapp.BotCommand.getAllCommands;
import static pl.miloszgilga.franekbotapp.ConfigurationLoader.config;


public class MismatchCommandInterceptor extends ListenerAdapter {

    private final LoggerFactory logger = new LoggerFactory(MismatchCommandInterceptor.class);
    private final List<String> allCommands = getAllCommands();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            if (!event.getAuthor().isBot() && event.getMessage().getContentRaw().contains(config.getDefPrefix())) {
                List<String> prefixAndArgs = Arrays.stream(event.getMessage().getContentRaw().split(" "))
                        .collect(Collectors.toList());

                String commandName = prefixAndArgs.get(0).replace(config.getDefPrefix(), "");
                if (allCommands.stream().noneMatch(el -> el.equals(commandName))) {
                    throw new UnrecognizedCommandException(event);
                }
            }
        } catch (UnrecognizedCommandException ex) {
            logger.error(ex.getMessage(), event.getGuild());
        }
    }
}