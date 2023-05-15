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
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandClient;

import java.util.*;
import org.apache.commons.lang3.StringUtils;

import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.QueueAfterParam;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class CommandEventWrapper {
    private final Guild guild;
    private final String guildName;
    private final String authorTag;
    private final String authorAvatarUrl;
    private final TextChannel textChannel;
    private final Member dataSender;
    private final String memberId;
    private final String guildId;
    private final User author;
    private final Member member;
    private CommandClient client;
    private String message;
    private Map<BotCommandArgument, String> args = new HashMap<>();
    private List<MessageEmbed> embeds= new ArrayList<>();
    private Runnable appendAfterEmbeds;
    private QueueAfterParam queueAfterParam;
    private boolean isFromSlashCommand = false;
    private SlashCommandEvent slashCommandEvent;
    private boolean isSended = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandEventWrapper(CommandEvent event) {
        guild = event.getGuild();
        guildName = event.getGuild().getName();
        authorTag = event.getAuthor().getAsTag();
        authorAvatarUrl = Objects.requireNonNullElse(event.getAuthor().getAvatarUrl(), event.getAuthor().getDefaultAvatarUrl());
        textChannel = event.getTextChannel();
        dataSender = event.getGuild().getMember(event.getAuthor());
        author = event.getAuthor();
        member = event.getMember();
        memberId = event.getMember().getId();
        guildId = event.getGuild().getId();
        client = event.getClient();
        message = event.getMessage().getContentRaw();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandEventWrapper(GuildMessageReceivedEvent event) {
        guild = event.getGuild();
        guildName = event.getGuild().getName();
        authorTag = event.getAuthor().getAsTag();
        authorAvatarUrl = Objects.requireNonNullElse(event.getAuthor().getAvatarUrl(), event.getAuthor().getDefaultAvatarUrl());
        textChannel = event.getChannel();
        dataSender = event.getGuild().getMember(event.getAuthor());
        author = event.getAuthor();
        member = event.getMember();
        memberId = Objects.isNull(event.getMember()) ? null : event.getMember().getId();
        guildId = event.getGuild().getId();
        message = event.getMessage().getContentRaw();
        isFromSlashCommand = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandEventWrapper(SlashCommandEvent event) {
        guild = event.getGuild();
        guildName = Objects.requireNonNull(event.getGuild()).getName();
        authorTag = Objects.requireNonNull(event.getMember()).getUser().getAsTag();
        authorAvatarUrl = Objects.requireNonNullElse(event.getMember().getUser().getAvatarUrl(),
            event.getMember().getUser().getDefaultAvatarUrl());
        textChannel = event.getTextChannel();
        dataSender = event.getGuild().getMember(event.getMember().getUser());
        author = event.getMember().getUser();
        member = event.getMember();
        memberId = event.getMember().getId();
        guildId = event.getGuild().getId();
        isFromSlashCommand = true;
        slashCommandEvent = event;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendEmbedMessage(MessageEmbed messageEmbed, QueueAfterParam deffer) {
        if (isFromSlashCommand && !slashCommandEvent.getHook().isExpired()) {
            slashCommandEvent.getHook()
                .sendMessageEmbeds(messageEmbed).queueAfter(deffer.duration(), deffer.timeUnit());
            return;
        }
        textChannel.sendMessageEmbeds(messageEmbed).queueAfter(deffer.duration(), deffer.timeUnit());
    }

    public void sendEmbedMessage(MessageEmbed messageEmbed) {
        sendEmbedMessage(messageEmbed, new QueueAfterParam());
    }

    public void appendEmbedMessage(MessageEmbed messageEmbed) {
        this.embeds.add(messageEmbed);
    }

    public void appendEmbedMessage(MessageEmbed messageEmbed, Runnable appendAfterEmbeds) {
        appendEmbedMessage(messageEmbed);
        this.appendAfterEmbeds = appendAfterEmbeds;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <T> T getArgumentAndParse(BotCommandArgument argument) {
        final String argumentValue = args.entrySet().stream()
            .filter(a -> a.getKey().equals(argument))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(StringUtils.EMPTY);
        return argument.parse(argumentValue);
    }
}
