/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ChannelLoggerCategoryLoader.java
 * Last modified: 08/08/2022, 22:07
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

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.lang.reflect.MalformedParametersException;

import pl.miloszgilga.franekbotapp.configuration.ChannelLoggerConfiguration;
import pl.miloszgilga.franekbotapp.configuration.ChannelLoggerChannelConfiguration;

import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


class ChannelLoggerCategoryLoader {

    private static final ChannelLoggerConfiguration loggerConfig = config.getChannelLoggerConfiguration();
    private static volatile ChannelLoggerCategoryLoader instance;

    private ChannelLoggerCategoryLoader() {
        if (instance != null) throw new IllegalArgumentException();
    }

    Category checkAndCreateLoggerChannelsCategoryIfNotExist(final Guild guild) {
        if (loggerConfig.getLoggerChannels().size() < ChannelType.getSizeOfAllChannels()) {
            throw new MalformedParametersException();
        }
        final String categoryName = loggerConfig.getLoggerChannelsCategoryName();
        final List<Category> categories = guild.getCategoriesByName(categoryName, true);

        if (categories.size() > 1) throw new IllegalArgumentException();
        if (categories.isEmpty()) {
            return guild.createCategory(categoryName)
                    .addMemberPermissionOverride(Objects.requireNonNull(guild.getBotRole()).getIdLong(),
                            EnumSet.of(Permission.ADMINISTRATOR), null)
                    .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .complete();
        }
        return guild.getCategoriesByName(categoryName, true).get(0);
    }

    void checkAndCreateChannelsIfNotExist(final Category category, final ChannelLoggerChannelConfiguration channel) {
        final Optional<TextChannel> findTextChannel = category.getTextChannels().stream()
                .filter(c -> c.getName().equalsIgnoreCase(channel.getChannelName())).findFirst();
        if (!channel.isEnabled() || findTextChannel.isPresent()) return;
        category.createTextChannel(channel.getChannelName()).complete();
    }

    static synchronized ChannelLoggerCategoryLoader getSingletonInstance() {
        if (instance == null) {
            instance = new ChannelLoggerCategoryLoader();
        }
        return instance;
    }
}