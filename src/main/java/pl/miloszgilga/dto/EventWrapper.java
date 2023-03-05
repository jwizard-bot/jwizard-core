/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ExceptionEventWrapper.java
 * Last modified: 05/03/2023, 22:17
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

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record EventWrapper(
    String guildName,
    String authorTag,
    String authorAvatarUrl,
    String message
) {
    public EventWrapper(CommandEvent event) {
        this(event.getGuild().getName(),
            event.getAuthor().getAsTag(),
            event.getAuthor().getAvatarUrl(),
            event.getMessage().getContentRaw()
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EventWrapper(GuildMessageReceivedEvent event) {
        this(event.getGuild().getName(),
            event.getAuthor().getAsTag(),
            event.getAuthor().getAvatarUrl(),
            event.getMessage().getContentRaw());
    }
}
