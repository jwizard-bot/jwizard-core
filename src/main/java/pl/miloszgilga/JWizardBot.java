/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: Bootloader.java
 * Last modified: 22/02/2023, 23:59
 * Project name: jwizard-discord-bot
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

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.Permission;
import org.springframework.stereotype.Component;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import pl.miloszgilga.core.loader.JClassLoader;
import pl.miloszgilga.core.db.HibernateFactory;
import pl.miloszgilga.misc.ActivityStatusSequencer;
import pl.miloszgilga.core.configuration.BotConfiguration;

import java.util.*;
import javax.security.auth.login.LoginException;

import static pl.miloszgilga.BotCommand.HELP_ME;
import static pl.miloszgilga.core.configuration.BotProperty.*;

import static net.dv8tion.jda.api.Permission.*;
import static net.dv8tion.jda.api.OnlineStatus.ONLINE;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;
import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.entities.Activity.listening;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class JWizardBot {

    private final BotConfiguration config;
    private final JClassLoader jClassLoader;
    private final ActivityStatusSequencer statusSequencer;
    private final HibernateFactory hibernateFactory;

    private static final int LINKED_CACHE_SIZE = 200;

    private static final GatewayIntent[] GATEWAY_INTENTS = {
        DIRECT_MESSAGES, GUILD_MESSAGES, GUILD_MESSAGE_REACTIONS, GUILD_VOICE_STATES, GUILD_MESSAGE_TYPING
    };

    public static final Permission[] PERMISSIONS = {
        MESSAGE_READ, MESSAGE_WRITE, MESSAGE_HISTORY, MESSAGE_ADD_REACTION, MESSAGE_EMBED_LINKS, MESSAGE_ATTACH_FILES,
        MESSAGE_MANAGE, MESSAGE_EXT_EMOJI, MANAGE_CHANNEL, VOICE_CONNECT, VOICE_SPEAK
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    JWizardBot(
        BotConfiguration config, JClassLoader jClassLoader, ActivityStatusSequencer statusSequencer,
        HibernateFactory hibernateFactory
    ) {
        this.config = config;
        this.jClassLoader = jClassLoader;
        this.statusSequencer = statusSequencer;
        this.hibernateFactory = hibernateFactory;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void run(String[] args) {
        config.loadConfiguration(args);
        config.printFancyTitle();
        config.printAdditionalInformations();

        jClassLoader.loadCommandsViaReflection();
        jClassLoader.loadListenersViaReflection();

        hibernateFactory.loadConfiguration();
        hibernateFactory.initialize();

        final CommandClientBuilder commandBuilder = new CommandClientBuilder()
            .setPrefix(config.getProperty(J_PREFIX))
            .setOwnerId(config.getProperty(J_APP_ID))
            .setHelpWord(HELP_ME.getName())
            .setLinkedCacheSize(LINKED_CACHE_SIZE)
            .addCommands(jClassLoader.getLoadedCommands());

        try {
            final JDA jda = JDABuilder
                .create(config.getProperty(J_AUTH_TOKEN), Arrays.asList(GATEWAY_INTENTS))
                .enableCache(MEMBER_OVERRIDES, VOICE_STATE)
                .disableCache(ACTIVITY, CLIENT_STATUS, EMOTE, ONLINE_STATUS)
                .setActivity(listening("Loading..."))
                .setStatus(ONLINE)
                .setBulkDeleteSplittingEnabled(true)
                .addEventListeners(jClassLoader.getAllListeners(commandBuilder.build()))
                .build();

            jda.awaitReady();

            statusSequencer.loadConfiguration(jda);
            statusSequencer.invoke();

            log.info("Add bot into Discord server via link: {}", jda.getInviteUrl(PERMISSIONS));
            log.info("Started listening incoming requests...");

        } catch (LoginException ex) {
            throw new RuntimeException("Unable to login via passed token and application id parameters.");
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Unexpected error occured. Cause: " + ex.getMessage());
        } catch (InterruptedException ex) {
            throw new RuntimeException("JDA Websocket threadpool connecting was interrupted. Cause: " + ex.getMessage());
        }
    }
}
