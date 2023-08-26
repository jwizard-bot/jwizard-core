/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JoinEmbedInteractions.java
 * Last modified: 19/06/2023, 16:44
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

package pl.miloszgilga.embed;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.remote.RemoteModuleProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;

import pl.miloszgilga.domain.guild.GuildEntity;
import pl.miloszgilga.domain.guild.IGuildRepository;
import pl.miloszgilga.domain.memory_messages.MessageType;
import pl.miloszgilga.domain.memory_messages.MemoryMessageEntity;
import pl.miloszgilga.domain.memory_messages.IMemoryMessageRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbedInteractionsOnJoin {

    private final IGuildRepository guildRepository;
    private final IMemoryMessageRepository memoryMessageRepository;

    private final RemotePropertyHandler handler;
    private final BotConfiguration config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Transactional
    public void addInitialInteractions(Guild providedGuild) {
        final Optional<GuildEntity> guildEntity = guildRepository.findByDiscordId(providedGuild.getId());
        if (guildEntity.isEmpty()) return;

        if (memoryMessageRepository.existsByGuildDiscordId(providedGuild.getId())) {
            updateAllInteractions(providedGuild);
            return;
        }
        final GuildEntity guild = guildEntity.get();
        final TextChannel responseChannel = Utilities.getSystemTextChannel(providedGuild);
        final String chId = responseChannel.getId();

        final Message welcomeMess = responseChannel.sendMessageEmbeds(createWelcomeMessage(providedGuild)).complete();
        final Message commandsMess = responseChannel.sendMessageEmbeds(createCommandsMessage(providedGuild)).complete();
        final Message statsMess = responseChannel.sendMessageEmbeds(createStatsMessage(providedGuild)).complete();

        final MemoryMessageEntity welcomeMessage = new MemoryMessageEntity(welcomeMess.getId(), chId, MessageType.WELCOME);
        final MemoryMessageEntity commandsMessage = new MemoryMessageEntity(commandsMess.getId(), chId, MessageType.COMMANDS);
        final MemoryMessageEntity statsMessage = new MemoryMessageEntity(statsMess.getId(), chId, MessageType.STATISTICS);

        guild.addMemoryMessage(welcomeMessage);
        guild.addMemoryMessage(commandsMessage);
        guild.addMemoryMessage(statsMessage);

        guildRepository.save(guild);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void modifyStatsModuleInteractionMessage(Guild providedGuild, boolean isActive) {
        memoryMessageRepository.findByGuild_DiscordIdAndMessageType(providedGuild.getId(), MessageType.STATISTICS)
            .ifPresent(message -> updateMessage(message, providedGuild, createStatsMessage(providedGuild, isActive)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void updateAllInteractions(Guild providedGuild) {
        memoryMessageRepository.findByGuild_DiscordIdAndMessageType(providedGuild.getId(), MessageType.WELCOME)
            .ifPresent(message -> updateMessage(message, providedGuild, createWelcomeMessage(providedGuild)));
        memoryMessageRepository.findByGuild_DiscordIdAndMessageType(providedGuild.getId(), MessageType.COMMANDS)
            .ifPresent(message -> updateMessage(message, providedGuild, createCommandsMessage(providedGuild)));
        memoryMessageRepository.findByGuild_DiscordIdAndMessageType(providedGuild.getId(), MessageType.STATISTICS)
            .ifPresent(message -> updateMessage(message, providedGuild, createStatsMessage(providedGuild)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MessageEmbed createWelcomeMessage(Guild guild) {
        final String welcomeMessage =
            String.format("**%s**", config.getLocaleText(ResLocaleSet.WELCOME_MESSAGE_WELCOME_HEADER, guild)) +
            "\n" +
            config.getLocaleText(ResLocaleSet.WELCOME_MESSAGE_BASE_CONTENT, guild) +
            "\n\n";

        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(welcomeMessage);
        embedBuilder.setColor(EmbedColor.ANTIQUE_WHITE.getColor());

        final String thumbnailUrl = guild.getJDA().getSelfUser().getAvatarUrl();
        if (!Objects.isNull(thumbnailUrl)) {
            embedBuilder.setThumbnail(thumbnailUrl);
        }
        return embedBuilder.build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MessageEmbed createCommandsMessage(Guild guild) {
        final StringBuilder welcomeBuilder = new StringBuilder();

        welcomeBuilder.append(String.format("**%s**", config.getLocaleText(ResLocaleSet.WELCOME_MESSAGE_COMMANDS_HEADER,
            guild)));
        welcomeBuilder.append("\n");

        for (final BotCommand command : BotCommand.getBasicCommands()) {
            welcomeBuilder.append(String.format("`%s` - ", command.parseWithPrefix(config)));
            welcomeBuilder.append(config.getLocaleText(command.getDescriptionLocaleSet()));
            welcomeBuilder.append('\n');
        }
        welcomeBuilder.append('\n');
        welcomeBuilder.append(config.getLocaleText(ResLocaleSet.WELCOME_MESSAGE_COMMANDS_CONTENT, guild, Map.of(
            "countOfCommands", BotCommand.count(),
            "helpCmd", BotCommand.HELP.parseWithPrefix(config),
            "helpMeCmd", BotCommand.HELP_ME.parseWithPrefix(config),
            "websiteLink", config.getProperty(BotProperty.J_WEBSITE_LINK)
        )));
        welcomeBuilder.append("\n\n");
        welcomeBuilder.append(config.getLocaleText(ResLocaleSet.WELCOME_MESSAGE_CONTRIBUTE_CONTENT, guild, Map.of(
            "sourceCodeLink", config.getProperty(BotProperty.J_SOURCE_CODE_PATH),
            "contributeEmail", config.getProperty(BotProperty.J_CONTRIBUTE_EMAIL)
        )));
        return new EmbedBuilder()
            .setDescription(welcomeBuilder.toString())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MessageEmbed createStatsMessage(Guild guild, boolean isActive) {
        final String statsMessage =
            String.format("**%s**", config.getLocaleText(ResLocaleSet.STATS_MESSAGE_STATS_HEADER, guild)) +
            "\n" +
            config.getLocaleText(ResLocaleSet.STATS_MESSAGE_STATS_CONTENT, guild, Map.of(
                "statsModuleState", config.getLocaleText(isActive ? ResLocaleSet.TURN_ON_MESS : ResLocaleSet.TURN_OFF_MESS)
                    .toLowerCase(),
                "disableStatsCmd", BotCommand.DISABLE_STATS.parseWithPrefix(config)
            ));
        return new EmbedBuilder()
            .setDescription(statsMessage)
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    private MessageEmbed createStatsMessage(Guild guild) {
        final boolean isStatsActive = handler
            .getPossibleRemoteModuleProperty(RemoteModuleProperty.R_STATS_MODULE_ENABLED, guild);
        return createStatsMessage(guild, isStatsActive);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateMessage(MemoryMessageEntity memoryMessage, Guild guild, MessageEmbed messageEmbed) {
        final TextChannel textChannel = guild.getTextChannelById(memoryMessage.getChannelId());
        if (Objects.isNull(textChannel)) return;

        final String guildName = guild.getName();
        final MessageType type = memoryMessage.getMessageType();

        textChannel.editMessageEmbedsById(memoryMessage.getMessageId(), messageEmbed).queue(
            ignored -> log.info("Message '{}' from guild '{}' was successfully updated", type, guildName),
            error -> log.error("Failure updating message '{}' from guild '{}'. Cause: {}", type, guildName,
                error.getMessage())
        );
    }
}
