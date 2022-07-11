/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SendMessageInterceptor.java
 * Last modified: 11/07/2022, 21:29
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

package pl.miloszgilga.interceptors;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

import pl.miloszgilga.AvailableCommands;
import pl.miloszgilga.exceptions.UnrecognizedCommandException;
import static pl.miloszgilga.FranekBot.DEF_PREFIX;
import static pl.miloszgilga.AvailableCommands.getAllCommands;


public class SendMessageInterceptor {

    private final List<String> allCommands = getAllCommands();
    private static SendMessageInterceptor instance;

    private SendMessageInterceptor() { }

    public List<String> validateRequestWithCommandType(MessageReceivedEvent event, AvailableCommands command) {
        try {
            if (!event.getAuthor().isBot() && event.getMessage().getContentRaw().contains(DEF_PREFIX)) {
                List<String> prefixAndArgs = Arrays.stream(event.getMessage().getContentRaw().split(" "))
                        .collect(Collectors.toList());

                String commandName = prefixAndArgs.get(0).replace(DEF_PREFIX, "");
                if (allCommands.stream().noneMatch(el -> el.equals(commandName))) {
                    throw new UnrecognizedCommandException(event);
                }
                if (command.getCommandName().equals(commandName)) {
                    return prefixAndArgs;
                }
            }
        } catch (UnrecognizedCommandException ex) {
            System.out.println(ex.getMessage());
        }
        return new ArrayList<>();
    }

    public static SendMessageInterceptor getSingletonInstance() {
        if (instance == null) {
            instance = new SendMessageInterceptor();
        }
        return instance;
    }
}