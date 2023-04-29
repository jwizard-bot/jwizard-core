/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildStatsCmd.java
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

import net.dv8tion.jda.api.entities.MessageEmbed;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.GuildMembersStatsDto;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractStatsCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import pl.miloszgilga.domain.guild_stats.IGuildStatsRepository;
import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;

import pl.miloszgilga.exception.StatsException.GuildHasNoStatsYetException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class GuildStatsCmd extends AbstractStatsCommand {

    private final IGuildStatsRepository statsRepository;
    private final IMemberStatsRepository memberStatsRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    GuildStatsCmd(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IGuildStatsRepository statsRepository,
        IMemberStatsRepository memberStatsRepository, RemotePropertyHandler handler
    ) {
        super(BotCommand.GUILD_STATS, config, embedBuilder, handler);
        this.statsRepository = statsRepository;
        this.memberStatsRepository = memberStatsRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteStatsCommand(CommandEventWrapper event) {
        statsRepository.findByGuild_DiscordId(event.getGuildId()).ifPresentOrElse(
            guildStats -> {
                final GuildMembersStatsDto statsDto = memberStatsRepository.getAllMemberStats(event.getGuildId());
                final MessageEmbed messageEmbed = embedBuilder.createGuildStatsMessage(event, guildStats, statsDto);
                event.sendEmbedMessage(messageEmbed);
            },
            () -> { throw new GuildHasNoStatsYetException(config, event); }
        );
    }
}
