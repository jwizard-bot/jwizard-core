/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: FranekBot.java
 * Last modified: 16/07/2022, 17:40
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

package pl.miloszgilga.franekbotapp;

import org.reflections.Reflections;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import javax.security.auth.login.LoginException;

import pl.miloszgilga.franekbotapp.interceptors.MismatchCommandInterceptor;
import pl.miloszgilga.franekbotapp.interceptors.ServerBotDeafenInterceptor;
import pl.miloszgilga.franekbotapp.logger.LoggerFactory;

import static pl.miloszgilga.franekbotapp.BotCommand.HELP_ME;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.checkIfItsDevelopmentVersion;


public class FranekBot {

    private static final FancyTitleGenerator generator = FancyTitleGenerator.getSingleton();
    private static final String EXECUTORS_PACKAGE = "pl.miloszgilga.franekbotapp.executors";

    public static void main(String[] args) throws LoginException, IOException {
        checkIfItsDevelopmentVersion(args);

        System.out.printf("FranekBot by Miłosz Gilga (https://github.com/Milosz08/JDA_Discord_Bot), wersja v%s%n",
                config.getBotVersion());
        if (config.isShowFancyTitle()) generator.generateFancyTitle();

        final String BOT_ID = config.getAuthorization().getApplicationId();
        final String BOT_TOKEN = config.getAuthorization().getToken();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix(config.getPrefix());
        builder.setOwnerId(BOT_ID);
        builder.setHelpWord(HELP_ME.getCommandName());
        builder.addCommands(reflectAllCommandExecutors());

        JDABuilder
                .createDefault(BOT_TOKEN)
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.listening(config.getPrefix() + HELP_ME.getCommandName()))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(
                        builder.build(),
                        new MismatchCommandInterceptor(),
                        new ServerBotDeafenInterceptor()
                )
                .build();
    }

    private static Command[] reflectAllCommandExecutors() {
        final LoggerFactory logger = new LoggerFactory(FranekBot.class);
        Reflections reflections = new Reflections(EXECUTORS_PACKAGE);

        Set<Class<? extends Command>> executorsClazz = reflections.getSubTypesOf(Command.class);
        Set<Command> executors = new HashSet<>();
        executorsClazz.forEach(clazz -> {
            try {
                executors.add(clazz.getDeclaredConstructor().newInstance());
                logger.debug(String.format("Egzekutor '%s' załadowany pomyślnie poprzez mechanizm refleksji",
                        clazz.getSimpleName()), null);
            } catch (Exception ignored) {
                logger.error(String.format("Wystąpił problem z załadowaniem egzekutora '%s' poprzez mechanizm refleksji",
                        clazz.getSimpleName()), null);
            }
        });

        return executors.toArray(Command[]::new);
    }
}