/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberStatsCommandListener.java
 * Last modified: 29/04/2023, 02:42
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

package pl.miloszgilga.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;

import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.remote.RemoteModuleProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

import pl.miloszgilga.domain.guild.GuildEntity;
import pl.miloszgilga.domain.guild.IGuildRepository;
import pl.miloszgilga.domain.member.MemberEntity;
import pl.miloszgilga.domain.member.IMemberRepository;
import pl.miloszgilga.domain.member_stats.MemberStatsEntity;
import pl.miloszgilga.domain.member_stats.IMemberStatsRepository;
import pl.miloszgilga.domain.member_settings.MemberSettingsEntity;
import pl.miloszgilga.domain.member_settings.IMemberSettingsRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableListenerLazyService
public class MemberStatsCommandListener extends AbstractListenerAdapter {

    private final IMemberStatsRepository statsRepository;
    private final IGuildRepository guildRepository;
    private final IMemberRepository memberRepository;
    private final IMemberSettingsRepository settingsRepository;
    private final RemotePropertyHandler handler;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MemberStatsCommandListener(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository statsRepository,
        IMemberSettingsRepository settingsRepository, IGuildRepository guildRepository, IMemberRepository memberRepository,
        RemotePropertyHandler handler
    ) {
        super(config, embedBuilder);
        this.statsRepository = statsRepository;
        this.settingsRepository = settingsRepository;
        this.guildRepository = guildRepository;
        this.memberRepository = memberRepository;
        this.handler = handler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addedMessagesIncCounter(GuildMessageReceivedEvent event) {
        if (Objects.isNull(event.getMember())) return;
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        createMemberStatsIfNotExist(event.getMember(), event.getGuild());
        statsRepository.increaseSendedMessages(event.getMember().getId(), event.getGuild().getId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updatedMessagesIncCounter(GuildMessageUpdateEvent event) {
        if (Objects.isNull(event.getMember())) return;
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        createMemberStatsIfNotExist(event.getMember(), event.getGuild());
        statsRepository.increaseUpdatedMessages(event.getMember().getId(), event.getGuild().getId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addedReactionsIncCouter(GuildMessageReactionAddEvent event) {
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        createMemberStatsIfNotExist(event.getMember(), event.getGuild());
        statsRepository.increaseAddedReactions(event.getMember().getId(), event.getGuild().getId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void slashInteractionsIncCounter(SlashCommandEvent event) {
        if (Objects.isNull(event.getMember()) || Objects.isNull(event.getGuild())) return;
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        createMemberStatsIfNotExist(event.getMember(), event.getGuild());
        statsRepository.increaseSlashInteractions(event.getMember().getId(), event.getGuild().getId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateNickname(GuildMemberUpdateNicknameEvent event) {
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        createMemberStatsIfNotExist(event.getMember(), event.getGuild());
        statsRepository.changeNickname(event.getMember().getId(), event.getGuild().getId(), event.getNewNickname());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void deleteMemberStatsAndSettings(GuildMemberRemoveEvent event) {
        if (Objects.isNull(event.getMember())) return;

        final String memberId = event.getMember().getId();
        final String guildId = event.getGuild().getId();

        statsRepository.deleteByMember_DiscordIdAndGuild_DiscordId(memberId, guildId);
        settingsRepository.deleteByMember_DiscordIdAndGuild_DiscordId(memberId, guildId);
        memberRepository.deleteOrphanMembers(memberId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "MemberSettingsCache", key = "#p0.id.concat(#p1.id)")
    public MemberSettingsEntity createMemberStatsIfNotExist(Member member, Guild guild) {
        if (statsRepository.existsByMember_DiscordIdAndGuild_DiscordId(member.getId(), guild.getId())) return null;

        final Optional<GuildEntity> optionalGuildEntity = guildRepository.getGuildByDiscordIdJoinLazy(guild.getId());
        if (optionalGuildEntity.isEmpty()) return null;

        final GuildEntity guildEntity = optionalGuildEntity.get();
        final MemberEntity memberEntity = memberRepository.findByDiscordIdJoinSettingsAndStats(member.getId())
            .orElseGet(() -> new MemberEntity(member.getId()));

        final MemberStatsEntity memberStats = new MemberStatsEntity(member);
        final MemberSettingsEntity memberSettings = new MemberSettingsEntity();

        memberEntity.addMemberStats(memberStats);
        memberEntity.addMemberSettings(memberSettings);
        guildEntity.addMemberGuildStats(memberStats);
        guildEntity.addMemberGuildSettings(memberSettings);

        memberRepository.save(memberEntity);
        return memberSettings;
    }

    private boolean checkIfStatsAreDisabled(Member member, Guild guild) {
        final Optional<MemberSettingsEntity> optionalSettings = settingsRepository
            .findByMember_DiscordIdAndGuild_DiscordId(member.getId(), guild.getId());
        if (optionalSettings.isEmpty()) {
            return member.getUser().isBot();
        }
        final MemberSettingsEntity settings = optionalSettings.get();
        final boolean isTurnOff = !handler
            .getPossibleRemoteModuleProperty(RemoteModuleProperty.R_STATS_MODULE_ENABLED, guild);

        return member.getUser().isBot() || isTurnOff || settings.getStatsDisabled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void onGuildMessageReceived(GuildMessageReceivedEvent event)               { addedMessagesIncCounter(event); }
    @Override public void onSlashCommand(SlashCommandEvent event)                               { slashInteractionsIncCounter(event); }
    @Override public void onGuildMessageUpdate(GuildMessageUpdateEvent event)                   { updatedMessagesIncCounter(event); }
    @Override public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)         { addedReactionsIncCouter(event); }
    @Override public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event)     { updateNickname(event); }
    @Transactional @Override public void onGuildMemberRemove(GuildMemberRemoveEvent event)      { deleteMemberStatsAndSettings(event); }
}
