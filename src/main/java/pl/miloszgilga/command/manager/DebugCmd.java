/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: DebugCmd.java
 * Last modified: 23/03/2023, 02:49
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.command.manager;

import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;

import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;
import org.apache.commons.lang3.function.TriFunction;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.locale.DebugLocaleSet;
import pl.miloszgilga.system.SystemProperty;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.embed.EmbedPaginationBuilder;
import pl.miloszgilga.command.AbstractManagerCommand;
import pl.miloszgilga.core.IEnumerableLocaleSet;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class DebugCmd extends AbstractManagerCommand {

    private final EmbedPaginationBuilder pagination;

    private final BiFunction<IEnumerableLocaleSet, Guild, String> formatHeader = (key, guild) ->
        String.format("\n**%s**\n", config.getLocaleText(key, guild).toUpperCase());

    private final TriFunction<IEnumerableLocaleSet, String, Guild, String> formatProperty = (key, value, guild) ->
        String.format("  `%s` :: %s", config.getLocaleText(key, guild), value);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    DebugCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, EmbedPaginationBuilder pagination,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.DEBUG, config, embedBuilder, handler);
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
        final Guild g = event.getGuild();
        final List<String> debugData = new ArrayList<>(SystemProperty.getAllFormatted(config, formatter, g));

        debugData.add(formatHeader.apply(DebugLocaleSet.JVM_HEADER_DEBUG, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.JVM_XMX_MEMORY_DEBUG, String.format("%d MB", totalM), g));
        debugData.add(formatProperty.apply(DebugLocaleSet.JVM_USED_MEMORY_DEBUG, String.format("%d MB", usedM), g));

        debugData.add(formatHeader.apply(DebugLocaleSet.GENERAL_HEADER_DEBUG, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.BOT_VERSION_DEBUG, config.getProjectVersion(), g));
        debugData.add(formatProperty.apply(DebugLocaleSet.BOT_LOCALE_DEBUG, selectedLang, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.CURRENT_GUILD_OWNER_TAG_DEBUG, ownerTag, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.CURRENT_GUILD_ID_DEBUG, event.getGuild().getId(), g));

        debugData.add(formatHeader.apply(DebugLocaleSet.CONFIGURATION_HEADER_DEBUG, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.DEFAULT_PREFIX_DEBUG, config.getProperty(BotProperty.J_PREFIX), g));
        debugData.add(formatProperty.apply(DebugLocaleSet.ENABLE_SLASH_COMMANDS_DEBUG, slashStatus, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.VOTE_MAX_WAITING_TIME_DEBUG, votingInactivityTime, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.LEAVE_CHANNEL_WAITING_TIME_DEBUG, noTracksInactivityTime, g));

        debugData.add(formatHeader.apply(DebugLocaleSet.VERSIONS_HEADER_DEBUG, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.JDA_VERSION_DEBUG, JDAInfo.VERSION, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.JDA_UTILITIES_VERSION_DEBUG, JDAUtilitiesInfo.VERSION, g));
        debugData.add(formatProperty.apply(DebugLocaleSet.LAVAPLAYER_VERSION_DEBUG, PlayerLibrary.VERSION, g));

        final MessageEmbed messageEmbed = embedBuilder.createMessage(ResLocaleSet.DEBUG_DATA_MESS, g);
        final Paginator debugPagination = pagination.createDefaultPaginator(debugData);
        event.appendEmbedMessage(messageEmbed, () -> debugPagination.display(event.getTextChannel()));
    }
}
