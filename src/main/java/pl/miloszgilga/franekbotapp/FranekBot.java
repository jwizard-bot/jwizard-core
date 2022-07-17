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

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import javax.security.auth.login.LoginException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import pl.miloszgilga.franekbotapp.executors.audioplayer.*;
import pl.miloszgilga.franekbotapp.interceptors.MismatchCommandInterceptor;
import pl.miloszgilga.franekbotapp.interceptors.ServerBotDeafenInterceptor;

import static pl.miloszgilga.franekbotapp.Command.HELP_ME;


public class FranekBot {

    private static final InputStream FILE = FranekBot.class.getResourceAsStream("/config/config.json");
    private static final FancyTitleGenerator generator = FancyTitleGenerator.getSingleton();
    public static Configuration config;

    private static void loadConfiguration() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (FILE == null) {
            throw new FileNotFoundException("Plik konfiguracyjny nie istnieje!");
        }
        config = objectMapper.readValue(new String(FILE.readAllBytes()), Configuration.class);
    }

    public static void main(String[] args) throws LoginException, IOException {
        loadConfiguration();

        if (config.isShowFancyTitle()) generator.generateFancyTitle();
        System.out.println("\nFranekBot by Miłosz Gilga (https://github.com/Milosz08/JDA_Discord_Bot), wersja v1.0\n");

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix(config.getDefPrefix());
        builder.setOwnerId(config.getApplicationId());
        builder.setHelpWord(HELP_ME.getCommandName());
        builder.addCommands(
                new PlayTrackCommandExecutor(),
                new SkipTrackCommandExecutor(),
                new PauseTrackCommandExecutor(),
                new ResumeTrackCommandExecutor(),
                new RepeatTrackCommandExecutor(),
                new ShowAllQueueCommandExecutor(),
                new VoteSkipTrackCommandExecutor(),
                new SetTrackVolumeCommandExecutor(),
                new VoteQueueClearCommandExecutor(),
                new VoteQueueShuffleCommandExecutor(),
                new MoveBotToVoiceChannelCommandExecutor()
        );

        JDABuilder
                .createDefault(config.getToken())
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.listening(config.getDefPrefix() + HELP_ME.getCommandName()))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(
                        builder.build(),
                        new MismatchCommandInterceptor(),
                        new ServerBotDeafenInterceptor()
                )
                .build();
    }
}