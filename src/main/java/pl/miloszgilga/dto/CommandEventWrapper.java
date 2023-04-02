/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandEventWrapper.java
 * Last modified: 19/03/2023, 23:11
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

package pl.miloszgilga.dto;

import lombok.Data;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandClient;

import pl.miloszgilga.misc.QueueAfterParam;

import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.ArrayList;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class CommandEventWrapper {
    private final Guild guild;
    private final String guildName;
    private final String authorTag;
    private final String authorAvatarUrl;
    private final TextChannel textChannel;
    private final Member dataSender;
    private final User author;
    private final Member member;
    private CommandClient client;
    private String message;
    private List<String> args = new ArrayList<>();
    private List<MessageEmbed> embeds= new ArrayList<>();
    private Runnable appendAfterEmbeds;
    private QueueAfterParam queueAfterParam;
    private boolean isFromSlashCommand = false;
    private SlashCommandEvent slashCommandEvent;
    private boolean isSended = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandEventWrapper(CommandEvent event) {
        this.guild = event.getGuild();
        this.guildName = event.getGuild().getName();
        this.authorTag = event.getAuthor().getAsTag();
        this.authorAvatarUrl = Objects.requireNonNullElse(event.getAuthor().getAvatarUrl(), event.getAuthor().getDefaultAvatarUrl());
        this.textChannel = event.getTextChannel();
        this.dataSender = event.getGuild().getMember(event.getAuthor());
        this.author = event.getAuthor();
        this.member = event.getMember();
        this.client = event.getClient();
        this.message = event.getMessage().getContentRaw();
        this.args = Arrays.stream(event.getArgs().split("\\|")).toList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandEventWrapper(GuildMessageReceivedEvent event) {
        this.guild = event.getGuild();
        this.guildName = event.getGuild().getName();
        this.authorTag = event.getAuthor().getAsTag();
        this.authorAvatarUrl = Objects.requireNonNullElse(event.getAuthor().getAvatarUrl(), event.getAuthor().getDefaultAvatarUrl());
        this.textChannel = event.getChannel();
        this.dataSender = event.getGuild().getMember(event.getAuthor());
        this.author = event.getAuthor();
        this.member = event.getMember();
        this.message = event.getMessage().getContentRaw();
        this.isFromSlashCommand = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandEventWrapper(SlashCommandEvent event) {
        this.guild = event.getGuild();
        this.guildName = Objects.requireNonNull(event.getGuild()).getName();
        this.authorTag = Objects.requireNonNull(event.getMember()).getUser().getAsTag();
        this.authorAvatarUrl = Objects.requireNonNullElse(event.getMember().getUser().getAvatarUrl(),
            event.getMember().getUser().getDefaultAvatarUrl());
        this.textChannel = event.getTextChannel();
        this.dataSender = event.getGuild().getMember(event.getMember().getUser());
        this.author = event.getMember().getUser();
        this.member = event.getMember();
        this.args = event.getOptions().stream().map(OptionMapping::getAsString).toList();
        this.isFromSlashCommand = true;
        this.slashCommandEvent = event;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendEmbedMessage(MessageEmbed messageEmbed, QueueAfterParam deffer) {
        if (isFromSlashCommand && !slashCommandEvent.getHook().isExpired()) {
            slashCommandEvent.getHook()
                .sendMessageEmbeds(messageEmbed).completeAfter(deffer.duration(), deffer.timeUnit());
            return;
        }
        textChannel.sendMessageEmbeds(messageEmbed).completeAfter(deffer.duration(), deffer.timeUnit());
    }

    public void sendEmbedMessage(MessageEmbed messageEmbed) {
        sendEmbedMessage(messageEmbed, new QueueAfterParam());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void appendEmbedMessage(MessageEmbed messageEmbed) {
        this.embeds.add(messageEmbed);
    }

    public void appendEmbedMessage(MessageEmbed messageEmbed, Runnable appendAfterEmbeds) {
        appendEmbedMessage(messageEmbed);
        this.appendAfterEmbeds = appendAfterEmbeds;
    }
}
