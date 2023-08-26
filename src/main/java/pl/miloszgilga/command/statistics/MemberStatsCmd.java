/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberStatsCmd.java
 * Last modified: 18/05/2023, 13:35
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

package pl.miloszgilga.command.statistics;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.concurrent.atomic.AtomicReference;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractStatsCommand;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;
import pl.miloszgilga.domain.member_settings.MemberSettingsEntity;
import pl.miloszgilga.domain.member_settings.IMemberSettingsRepository;

import static pl.miloszgilga.exception.StatsException.GuildHasNoStatsYetException;
import static pl.miloszgilga.exception.StatsException.MemberHasDisableStatsException;
import static pl.miloszgilga.exception.StatsException.MemberHasPrivateStatsException;
import static pl.miloszgilga.exception.StatsException.MemberHasNoStatsYetInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class MemberStatsCmd extends AbstractStatsCommand {

    private final IMemberStatsRepository statsRepository;
    private final IMemberSettingsRepository settingsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MemberStatsCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository statsRepository,
        IMemberSettingsRepository settingsRepository, RemotePropertyHandler handler,
        CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.MEMBER_STATS, config, embedBuilder, handler, cacheableCommandStateDao);
        this.statsRepository = statsRepository;
        this.settingsRepository = settingsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteStatsCommand(CommandEventWrapper event) {
        final AtomicReference<String> userId = new AtomicReference<>();
        userId.set(event.getArgumentAndParse(BotCommandArgument.MEMBER_STATS_MEMBER_TAG));
        if (userId.get().contains("@")) {
            userId.set(userId.get().replaceAll("[@<>]", ""));
        }
        final User user = Utilities.checkIfMemberInGuildExist(event, userId.get(), config).getUser();
        statsRepository.findByMember_DiscordIdAndGuild_DiscordId(userId.get(), event.getGuildId()).ifPresentOrElse(
            memberStats -> {
                final MemberSettingsEntity settings = settingsRepository
                    .findByMember_DiscordIdAndGuild_DiscordId(userId.get(), event.getGuildId())
                    .orElseThrow(() -> new GuildHasNoStatsYetException(config, event));

                if (settings.getStatsDisabled()) {
                    throw new MemberHasDisableStatsException(config, event, user);
                }
                final boolean isNotOwner = !event.getAuthor().getId().equals(event.getGuild().getOwnerId());
                final boolean isNotManager = !event.getMember().hasPermission(Permission.MANAGE_SERVER);
                if (settings.getStatsPrivate() && (isNotOwner || isNotManager)) {
                    throw new MemberHasPrivateStatsException(config, event, user);
                }
                final MessageEmbed messageEmbed = embedBuilder.createMemberStatsMessage(event, memberStats);
                event.sendEmbedMessage(messageEmbed);
            },
            () -> { throw new MemberHasNoStatsYetInGuildException(config, event, user); }
        );
    }
}
