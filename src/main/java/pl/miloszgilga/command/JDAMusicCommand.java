/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioCommand.java
 * Last modified: 05/03/2023, 00:24
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

package pl.miloszgilga.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.JDACommand;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.AudioPlayerException.UserOnVoiceChannelNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class JDAMusicCommand extends JDACommand {

    protected VoiceChannel voiceChannel;
    protected final BotConfiguration config;

    public JDAMusicCommand(BotCommand command, BotConfiguration config, EmbedMessageBuilder embedBuilder) {
        super(command, config, embedBuilder);
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void execute(CommandEvent event) {
        final String guildId = event.getGuild().getId();
        this.voiceChannel = Objects.requireNonNull(event.getJDA().getGuildById(guildId)).getVoiceChannels().stream()
            .filter(channel -> {
                final Member sender = event.getGuild().getMember(event.getAuthor());
                final Member bot = event.getGuild().getMember(event.getJDA().getSelfUser());
                return channel.getMembers().contains(sender) && channel.getMembers().contains(bot);
            })
            .findFirst()
            .orElseThrow(() -> { throw new UserOnVoiceChannelNotFoundException(config, new EventWrapper(event)); });
    }
}
