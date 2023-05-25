/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: DebugCmd.java
 * Last modified: 17/05/2023, 01:42
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
import pl.miloszgilga.core.remote.RemoteProperty;
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
        final String votingInactivityTime = handler
            .getPossibleRemoteProperty(RemoteProperty.R_INACTIVITY_VOTING_TIMEOUT, event.getGuild()) + "s";
        final String noTracksInactivityTime = handler
            .getPossibleRemoteProperty(RemoteProperty.R_INACTIVITY_NO_TRACK_TIMEOUT, event.getGuild()) + "s";
        final String ownerTag = Objects.requireNonNull(event.getGuild().getOwner()).getUser().getAsTag();
        final String selectedLang = handler.getPossibleRemoteProperty(RemoteProperty.R_SELECTED_LOCALE, event.getGuild());
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
