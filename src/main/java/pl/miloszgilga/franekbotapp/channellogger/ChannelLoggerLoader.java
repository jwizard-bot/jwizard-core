/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ChannelLoggerLoader.java
 * Last modified: 08/08/2022, 20:08
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

package pl.miloszgilga.franekbotapp.channellogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Category;

import java.util.List;
import java.lang.reflect.MalformedParametersException;

import pl.miloszgilga.franekbotapp.configuration.ChannelLoggerConfiguration;
import pl.miloszgilga.franekbotapp.configuration.ChannelLoggerChannelConfiguration;

import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


public class ChannelLoggerLoader {

    private static final Logger logger = LoggerFactory.getLogger(ChannelLoggerLoader.class);
    private static final ChannelLoggerConfiguration loggerConfig = config.getChannelLoggerConfiguration();
    private static final ChannelLoggerCategoryLoader categoryLoader = ChannelLoggerCategoryLoader.getSingletonInstance();

    private static volatile ChannelLoggerLoader instance;
    private final JDA jda;

    private ChannelLoggerLoader(JDA jda) {
        if (instance != null) throw new IllegalArgumentException();
        this.jda = jda;
    }

    public void initialiseChannelLoggerConfiguration() {
        if (jda == null) return;

        final boolean ifAllLoggersEnabled = loggerConfig.getLoggerChannels()
                .stream().allMatch(ChannelLoggerChannelConfiguration::isEnabled);
        if (!loggerConfig.isLoggerEnabled() || !ifAllLoggersEnabled) {
            logger.info("Logger wiadomości serwerowych nieaktywny. Aby aktywować, edytuj plik konfiguracyjny");
            return;
        }

        createChannelsIfNotExist();
        logger.info("Inicjalizacja wewnętrznego loggera wiadomości systemowych zakończona sukcesem");
    }

    private void createChannelsIfNotExist() {
        try {
            final List<Guild> allBotGuilds = jda.awaitReady().getGuilds();
            for (Guild guild : allBotGuilds) {
                Category createdCategory = categoryLoader.checkAndCreateLoggerChannelsCategoryIfNotExist(guild);
                for (ChannelLoggerChannelConfiguration channel : loggerConfig.getLoggerChannels()) {
                    categoryLoader.checkAndCreateChannelsIfNotExist(createdCategory, channel);
                }
            }
        } catch (MalformedParametersException | NumberFormatException | InterruptedException ex) {
            logger.error("Nieudane załadowanie wewnętrznego loggera wiadomości systemowych");
        } catch (IllegalStateException ex) {
            logger.error("Nazwa grupy kanałów wewnętrzengo loggera wiadomości musi być unikalna");
        }
    }

    public static synchronized ChannelLoggerLoader getSingletonInstance(JDA jda) {
        if (instance == null) {
            instance = new ChannelLoggerLoader(jda);
        }
        return instance;
    }
}