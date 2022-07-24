/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: EventWrapper.java
 * Last modified: 24/07/2022, 18:45
 * Project name: franek-bot
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

package pl.miloszgilga.franekbotapp.audioplayer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventWrapper {
    private Guild guild;
    private JDA jda;
    private User user;
    private TextChannel textChannel;
    private String args;

    public EventWrapper(CommandEvent event) {
        guild = event.getGuild();
        jda = event.getJDA();
        user = event.getAuthor();
        textChannel = event.getTextChannel();
        args = event.getArgs();
    }

    public EventWrapper(GuildVoiceLeaveEvent event) {
        guild = event.getGuild();
        jda = event.getJDA();
    }
}