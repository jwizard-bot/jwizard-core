/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: FranekBot.java
 * Last modified: 11/07/2022, 22:32
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

package pl.miloszgilga;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import javax.security.auth.login.LoginException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import pl.miloszgilga.executors.*;
import static pl.miloszgilga.Command.HELP_ME;
import pl.miloszgilga.interceptors.MismatchCommandInterceptor;


public class FranekBot {

    public static Configuration config;
    private static final String FILENAME = "config.json";

    private static void loadConfiguration() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String filePath = "src/main/resources/config/" + FILENAME;
        config = objectMapper.readValue(new String(Files.readAllBytes(Paths.get(filePath))), Configuration.class);
    }

    public static void main(String[] args) throws LoginException, IOException {
        loadConfiguration();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix(config.getDefPrefix());
        builder.setOwnerId(config.getApplicationId());
        builder.setHelpWord(HELP_ME.getCommandName());
        builder.addCommands(
                new AudioPlayCommandExecutor(),
                new AudioSkippedCommandExecutor(),
                new AudioRepeatLoopCommandExecutor(),
                new AudioShowAllQueueCommandExecutor(),
                new AudioQueueShuffleCommandExecutor(),
                new AudioMoveBotToVoiceChannelCommandExecutor()
        );

        JDABuilder
                .createDefault(config.getToken())
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.listening(config.getDefPrefix() + HELP_ME.getCommandName()))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(
                        builder.build(),
                        new MismatchCommandInterceptor()
                )
                .build();
    }
}