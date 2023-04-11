/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ResetMemberStatsCmd.java
 * Last modified: 10/04/2023, 16:02
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
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository statsRepository
    ) {
        super(BotCommand.RESET_MEMBER_STATS, config, embedBuilder);
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
                    ));
                JDALog.info(log, event, "Stats of selected memeber '%s' was successfully cleared", user.getAsTag());
                event.sendEmbedMessage(messageEmbed);
            },
            () -> { throw new YouHasNoStatsYetInGuildException(config, event); }
        );
    }
}
