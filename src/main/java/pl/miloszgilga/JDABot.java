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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import org.springframework.stereotype.Component;

import java.util.*;
import javax.security.auth.login.LoginException;

import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.AloneOnChannelListener;
import pl.miloszgilga.core.loader.JClassLoader;
import pl.miloszgilga.core.db.HibernateFactory;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.misc.ActivityStatusSequencer;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class JDABot {

    private final BotConfiguration config;
    private final JClassLoader jClassLoader;
    private final ActivityStatusSequencer statusSequencer;
    private final HibernateFactory hibernateFactory;
    private final PlayerManager playerManager;
    private final AloneOnChannelListener aloneOnChannelListener;

    private static final int LINKED_CACHE_SIZE = 200;

    private static final GatewayIntent[] GATEWAY_INTENTS = {
        GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_TYPING
    };

    public static final Permission[] PERMISSIONS = {
        Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
        Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE,
        Permission.MESSAGE_EXT_EMOJI, Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK
    };

    private static final List<CacheFlag> ENABLED_CACHE_FLAGS = List.of(
        CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE
    );

    private static final List<CacheFlag> DISABLED_CACHE_FLAGS = List.of(
        CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ONLINE_STATUS
    );

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    JDABot(
        BotConfiguration config, JClassLoader jClassLoader, ActivityStatusSequencer statusSequencer,
        HibernateFactory hibernateFactory, PlayerManager playerManager, AloneOnChannelListener aloneOnChannelListener
    ) {
        this.config = config;
        this.jClassLoader = jClassLoader;
        this.statusSequencer = statusSequencer;
        this.hibernateFactory = hibernateFactory;
        this.playerManager = playerManager;
        this.aloneOnChannelListener = aloneOnChannelListener;
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
            .setPrefix(config.getProperty(BotProperty.J_PREFIX))
            .setOwnerId(config.getProperty(BotProperty.J_APP_ID))
            .setHelpWord(BotCommand.HELP_ME.getName())
            .setLinkedCacheSize(LINKED_CACHE_SIZE)
            .addCommands(jClassLoader.getLoadedCommands());

        try {
            final JDA jda = JDABuilder
                .create(config.getProperty(BotProperty.J_AUTH_TOKEN), Arrays.asList(GATEWAY_INTENTS))
                .enableCache(ENABLED_CACHE_FLAGS)
                .disableCache(DISABLED_CACHE_FLAGS)
                .setActivity(Activity.listening("Loading..."))
                .setStatus(OnlineStatus.ONLINE)
                .setBulkDeleteSplittingEnabled(true)
                .addEventListeners(jClassLoader.getAllListeners(commandBuilder.build()))
                .build();

            jda.awaitReady();

            config.setTitleAndIcon(jda);

            statusSequencer.loadConfiguration(jda);
            statusSequencer.invoke();

            playerManager.initialize();
            aloneOnChannelListener.initialize(jda);

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
