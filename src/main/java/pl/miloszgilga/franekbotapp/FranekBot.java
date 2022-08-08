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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import java.util.*;
import java.io.IOException;
import javax.security.auth.login.LoginException;

import pl.miloszgilga.franekbotapp.database.HibernateSessionFactory;
import pl.miloszgilga.franekbotapp.channellogger.ChannelLoggerLoader;
import pl.miloszgilga.franekbotapp.channellogger.AuditableInterceptorsReflection;

import static pl.miloszgilga.franekbotapp.BotCommand.HELP_ME;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.checkIfItsDevelopmentVersion;


public final class FranekBot {

    private static final ElementsReflection reflection = ElementsReflection.getSingletonInstance();
    private static final FancyTitleGenerator generator = FancyTitleGenerator.getSingletonInstance();
    private static final AuditableInterceptorsReflection auditReflection = AuditableInterceptorsReflection.getSingletonInstance();

    public static void main(String[] args) throws LoginException, IOException {
        checkIfItsDevelopmentVersion(args);

        System.out.printf("FranekBot by Miłosz Gilga (https://github.com/Milosz08/JDA_Discord_Bot), wersja v%s%n",
                config.getBotVersion());
        if (config.isShowFancyTitle()) generator.generateFancyTitle();

        final Deque<Object> interceptors = new LinkedList<>();
        final String BOT_ID = config.getAuthorization().getApplicationId();
        final String BOT_TOKEN = config.getAuthorization().getToken();

        // initialise hibernate singleton instance
        HibernateSessionFactory.getSingletonInstance();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix(config.getPrefix());
        builder.setOwnerId(BOT_ID);
        builder.setHelpWord(HELP_ME.getCommandName());
        builder.addCommands(reflection.reflectAllCommandExecutors());
        interceptors.addFirst(builder.build());

        JDA jda = JDABuilder
                .createDefault(BOT_TOKEN)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.listening("Loading..."))
                .addEventListeners(reflection.reflectAllInterceptors(interceptors))
                .addEventListeners(auditReflection.reflectAllAuditableInterceptors())
                .build();

        // initialise threading bot activity status sequencer
        final BotActivitySequencer sequencer = BotActivitySequencer.getSingletonInstance(jda);
        sequencer.invokeSequencer();

        // initialise inside-logger channel configuration loader
        final ChannelLoggerLoader loggerLoader = ChannelLoggerLoader.getSingletonInstance(jda);
        loggerLoader.initialiseChannelLoggerConfiguration();
    }
}