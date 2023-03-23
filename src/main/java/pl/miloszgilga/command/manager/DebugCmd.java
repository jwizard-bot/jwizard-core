/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: DebugCmd.java
 * Last modified: 23/03/2023, 01:16
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

package pl.miloszgilga.command.manager;

import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;

import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.BiFunction;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.system.SystemProperty;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.embed.EmbedPaginationBuilder;
import pl.miloszgilga.command.AbstractManagerCommand;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class DebugCmd extends AbstractManagerCommand {

    private final EmbedPaginationBuilder pagination;

    private final Function<LocaleSet, String> formatHeader = key ->
        String.format("\n**%s**\n", config.getLocaleText(key).toUpperCase());

    private final BiFunction<LocaleSet, String, String> formatProperty = (key, value) ->
        String.format("  `%s` :: %s", config.getLocaleText(key), value);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    DebugCmd(BotConfiguration config, EmbedMessageBuilder embedBuilder, EmbedPaginationBuilder pagination) {
        super(BotCommand.DEBUG, config, embedBuilder);
        this.pagination = pagination;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteManagerCommand(CommandEventWrapper event) {
        final String slashStatus = config.getProperty(BotProperty.J_SLASH_COMMANDS_ENABLED, Boolean.class) ? "ON" : "OFF";
        final String votingInactivityTime = config.getProperty(BotProperty.J_INACTIVITY_VOTING_TIMEOUT) + "s";
        final String noTracksInactivityTime = config.getProperty(BotProperty.J_INACTIVITY_EMPTY_TIMEOUT) + "s";
        final String ownerTag = Objects.requireNonNull(event.getGuild().getOwner()).getUser().getAsTag();

        final long totalM = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        final long usedM = totalM - (Runtime.getRuntime().freeMemory() / 1024 / 1024);

        final BiFunction<String, String, String> formatter = (key, value) -> String.format("  `%s` :: %s", key, value);
        final List<String> debugData = new ArrayList<>(SystemProperty.getAllFormatted(config, formatter));

        debugData.add(formatHeader.apply(LocaleSet.JVM_HEADER_DEBUG));
        debugData.add(formatProperty.apply(LocaleSet.JVM_XMX_MEMORY_DEBUG, String.format("%d MB", totalM)));
        debugData.add(formatProperty.apply(LocaleSet.JVM_USED_MEMORY_DEBUG, String.format("%d MB", usedM)));

        debugData.add(formatHeader.apply(LocaleSet.GENERAL_HEADER_DEBUG));
        debugData.add(formatProperty.apply(LocaleSet.BOT_VERSION_DEBUG, config.getProjectVersion()));
        debugData.add(formatProperty.apply(LocaleSet.BOT_LOCALE_DEBUG, config.getProperty(BotProperty.J_SELECTED_LOCALE)));
        debugData.add(formatProperty.apply(LocaleSet.CURRENT_GUILD_OWNER_TAG_DEBUG, ownerTag));
        debugData.add(formatProperty.apply(LocaleSet.CURRENT_GUILD_ID_DEBUG, event.getGuild().getId()));

        debugData.add(formatHeader.apply(LocaleSet.CONFIGURATION_HEADER_DEBUG));
        debugData.add(formatProperty.apply(LocaleSet.DEFAULT_PREFIX_DEBUG, config.getProperty(BotProperty.J_PREFIX)));
        debugData.add(formatProperty.apply(LocaleSet.ENABLE_SLASH_COMMANDS_DEBUG, slashStatus));
        debugData.add(formatProperty.apply(LocaleSet.VOTE_MAX_WAITING_TIME_DEBUG, votingInactivityTime));
        debugData.add(formatProperty.apply(LocaleSet.LEAVE_CHANNEL_WAITING_TIME_DEBUG, noTracksInactivityTime));

        debugData.add(formatHeader.apply(LocaleSet.VERSIONS_HEADER_DEBUG));
        debugData.add(formatProperty.apply(LocaleSet.JDA_VERSION_DEBUG, JDAInfo.VERSION));
        debugData.add(formatProperty.apply(LocaleSet.JDA_UTILITIES_VERSION_DEBUG, JDAUtilitiesInfo.VERSION));
        debugData.add(formatProperty.apply(LocaleSet.LAVAPLAYER_VERSION_DEBUG, PlayerLibrary.VERSION));

        final MessageEmbed messageEmbed = embedBuilder.createMessage(LocaleSet.DEBUG_DATA_MESS);
        final Paginator debugPagination = pagination.createDefaultPaginator(debugData);
        event.appendEmbedMessage(messageEmbed, () -> debugPagination.display(event.getTextChannel()));
    }
}
