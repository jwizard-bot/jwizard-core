/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ResetMemberStatsCmd.java
 * Last modified: 16/05/2023, 18:50
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

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractManagerStatsCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;

import static pl.miloszgilga.exception.StatsException.YouHasNoStatsYetInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class ResetMemberStatsCmd extends AbstractManagerStatsCommand {

    private final IMemberStatsRepository statsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ResetMemberStatsCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository statsRepository,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.RESET_MEMBER_STATS, config, embedBuilder, handler);
        this.statsRepository = statsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteManagerStatsCommand(CommandEventWrapper event) {
        final String[] userId = new String[1];
        userId[0] = event.getArgumentAndParse(BotCommandArgument.RESET_MEMBER_STATS_MEMBER_TAG);
        if (userId[0].contains("@")) userId[0] = userId[0].replaceAll("[@<>]", "");

        final User user = Utilities.checkIfMemberInGuildExist(event, userId[0], config).getUser();
        statsRepository.findByMember_DiscordIdAndGuild_DiscordId(user.getId(), event.getGuildId()).ifPresentOrElse(
            memberStats -> {
                memberStats.resetStats();
                statsRepository.save(memberStats);

                final MessageEmbed messageEmbed = embedBuilder
                    .createMessage(ResLocaleSet.MEMBER_STATS_CLEARED_MESS, Map.of(
                        "memberTag", user.getAsTag(),
                        "guildName", event.getGuildName()
                    ), event.getGuild());
                JDALog.info(log, event, "Stats of selected memeber '%s' was successfully cleared", user.getAsTag());
                event.sendEmbedMessage(messageEmbed);
            },
            () -> { throw new YouHasNoStatsYetInGuildException(config, event); }
        );
    }
}
