/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EventCommandWrapper.java
 * Last modified: 18/03/2023, 21:17
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

package pl.miloszgilga.dto;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandClient;

import java.util.Objects;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record CommandEventWrapper(
    Guild guild,
    String guildName,
    String authorTag,
    String authorAvatarUrl,
    TextChannel textChannel,
    Member dataSender,
    User author,
    Member member,
    CommandClient client,
    String message,
    String args
) {
    public CommandEventWrapper(CommandEvent event) {
        this(
            event.getGuild(),
            event.getGuild().getName(),
            event.getAuthor().getAsTag(),
            Objects.requireNonNullElse(event.getAuthor().getAvatarUrl(), event.getAuthor().getDefaultAvatarUrl()),
            event.getTextChannel(),
            event.getGuild().getMember(event.getAuthor()),
            event.getAuthor(),
            event.getMember(),
            event.getClient(),
            event.getMessage().getContentRaw(),
            event.getArgs()
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandEventWrapper(GuildMessageReceivedEvent event) {
        this(
            event.getGuild(),
            event.getGuild().getName(),
            event.getAuthor().getAsTag(),
            Objects.requireNonNullElse(event.getAuthor().getAvatarUrl(), event.getAuthor().getDefaultAvatarUrl()),
            event.getChannel(),
            event.getGuild().getMember(event.getAuthor()),
            event.getAuthor(),
            event.getMember(),
            null,
            event.getMessage().getContentRaw(),
            null
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandEventWrapper(SlashCommandEvent event) {
        this(
            event.getGuild(),
            Objects.requireNonNull(event.getGuild()).getName(),
            Objects.requireNonNull(event.getMember()).getUser().getAsTag(),
            Objects.requireNonNullElse(event.getMember().getAvatarUrl(), event.getMember().getUser().getDefaultAvatarUrl()),
            event.getJDA().getTextChannelById(event.getChannel().getId()),
            event.getGuild().getMember(event.getMember().getUser()),
            event.getMember().getUser(),
            event.getMember(),
            null,
            null,
            null
        );
    }
}
