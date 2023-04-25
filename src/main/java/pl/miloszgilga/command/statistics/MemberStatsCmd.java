/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberStatsCmd.java
 * Last modified: 09/04/2023, 21:51
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

package pl.miloszgilga.command.statistics;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.MessageEmbed;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractStatsCommand;
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
        IMemberSettingsRepository settingsRepository
    ) {
        super(BotCommand.MEMBER_STATS, config, embedBuilder);
        this.statsRepository = statsRepository;
        this.settingsRepository = settingsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteStatsCommand(CommandEventWrapper event) {
        final String[] userId = new String[1];
        userId[0] = event.getArgumentAndParse(BotCommandArgument.MEMBER_STATS_MEMBER_TAG);
        if (userId[0].contains("@")) userId[0] = userId[0].replaceAll("[@<>]", "");

        final User user = Utilities.checkIfMemberInGuildExist(event, userId[0], config).getUser();
        statsRepository.findByMember_DiscordIdAndGuild_DiscordId(userId[0], event.getGuildId()).ifPresentOrElse(
            memberStats -> {
                final MemberSettingsEntity settings = settingsRepository
                    .findByMember_DiscordIdAndGuild_DiscordId(userId[0], event.getGuildId())
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
