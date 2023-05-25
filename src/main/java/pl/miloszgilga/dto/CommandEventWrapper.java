/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandEventWrapper.java
 * Last modified: 17/05/2023, 00:50
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

package pl.miloszgilga.dto;

import lombok.Data;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandClient;

import java.util.*;

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
            .orElse(null);
        if (Objects.isNull(argumentValue)) return null;
        return argument.parse(argumentValue);
    }
}
