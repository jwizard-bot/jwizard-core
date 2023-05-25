/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JDABot.java
 * Last modified: 17/05/2023, 12:41
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
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

import pl.miloszgilga.core.loader.JClassLoader;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.AloneOnChannelListener;
import pl.miloszgilga.misc.ActivityStatusSequencer;
import pl.miloszgilga.misc.DayNightBotThumbnailSequencer;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class JDABot {

    private final BotConfiguration config;
    private final JClassLoader jClassLoader;
    private final ActivityStatusSequencer statusSequencer;
    private final PlayerManager playerManager;
    private final AloneOnChannelListener aloneOnChannelListener;
    private final DayNightBotThumbnailSequencer dayNightBotThumbnailSequencer;

    private static final int LINKED_CACHE_SIZE = 200;

    private static final GatewayIntent[] GATEWAY_INTENTS = {
        GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MEMBERS
    };

    public static final Permission[] PERMISSIONS = {
        Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
        Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE,
        Permission.MESSAGE_EXT_EMOJI, Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
        Permission.USE_SLASH_COMMANDS, Permission.MANAGE_ROLES, Permission.VOICE_DEAF_OTHERS
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
        PlayerManager playerManager, AloneOnChannelListener aloneOnChannelListener,
        DayNightBotThumbnailSequencer dayNightBotThumbnailSequencer
    ) {
        this.config = config;
        this.jClassLoader = jClassLoader;
        this.statusSequencer = statusSequencer;
        this.playerManager = playerManager;
        this.aloneOnChannelListener = aloneOnChannelListener;
        this.dayNightBotThumbnailSequencer = dayNightBotThumbnailSequencer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void run() {
        config.loadConfiguration();
        config.printAdditionalInformations();

        jClassLoader.loadCommandsViaReflection();
        jClassLoader.loadListenersViaReflection();

        final CommandClientBuilder commandBuilder = new CommandClientBuilder()
            .setPrefix(config.getProperty(BotProperty.J_PREFIX))
            .setOwnerId(config.getProperty(BotProperty.J_APP_ID))
            .setHelpWord(BotCommand.HELP_ME.getName())
            .setLinkedCacheSize(LINKED_CACHE_SIZE)
            .addCommands(jClassLoader.getLoadedCommands());

        if (config.getProperty(BotProperty.J_SLASH_COMMANDS_ENABLED, Boolean.class)) {
            commandBuilder.addSlashCommands(jClassLoader.getLoadedCommands());
        }
        try {
            final JDA jda = JDABuilder
                .create(config.getProperty(BotProperty.J_AUTH_TOKEN), Arrays.asList(GATEWAY_INTENTS))
                .enableCache(ENABLED_CACHE_FLAGS)
                .disableCache(DISABLED_CACHE_FLAGS)
                .setActivity(Activity.listening("Loading..."))
                .setStatus(OnlineStatus.ONLINE)
                .setBulkDeleteSplittingEnabled(true)
                .addEventListeners(jClassLoader.getAllListeners(commandBuilder.build()))
                .build()
                .awaitReady();

            config.setTitleAndIcon(jda);

            statusSequencer.loadConfiguration(jda);
            statusSequencer.initializeComponent();

            dayNightBotThumbnailSequencer.loadConfiguration(jda);
            dayNightBotThumbnailSequencer.initializeComponent();

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
