/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberStatsCommandListener.java
 * Last modified: 09/04/2023, 18:43
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

import java.util.Objects;
import java.util.Optional;

import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.RemoteProperty;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MemberStatsCommandListener(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository statsRepository,
        IMemberSettingsRepository settingsRepository, IGuildRepository guildRepository, IMemberRepository memberRepository
    ) {
        super(config, embedBuilder);
        this.statsRepository = statsRepository;
        this.settingsRepository = settingsRepository;
        this.guildRepository = guildRepository;
        this.memberRepository = memberRepository;
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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "MemberSettingsCache", key = "#p0.id.concat(#p1.id)")
    public MemberSettingsEntity createMemberStatsIfNotExist(Member member, Guild guild) {
        if (statsRepository.existsByMember_DiscordIdAndGuild_DiscordId(member.getId(), guild.getId())) return null;

        final Optional<GuildEntity> guildEntity = guildRepository.findByDiscordId(guild.getId());
        if (guildEntity.isEmpty()) return null;

        final MemberEntity memberEntity = memberRepository.findByDiscordId(member.getId())
            .orElseGet(() -> memberRepository.save(new MemberEntity(member.getId())));

        final MemberStatsEntity memberStats = new MemberStatsEntity(guildEntity.get(), member, memberEntity);
        final MemberSettingsEntity memberSettings = new MemberSettingsEntity(memberEntity, guildEntity.get());

        statsRepository.save(memberStats);
        return settingsRepository.save(memberSettings);
    }

    private boolean checkIfStatsAreDisabled(Member member, Guild guild) {
        final Optional<MemberSettingsEntity> optionalSettings = settingsRepository
            .findByMember_DiscordIdAndGuild_DiscordId(member.getId(), guild.getId());
        if (optionalSettings.isEmpty()) {
            return member.getUser().isBot();
        }
        final MemberSettingsEntity settings = optionalSettings.get();
        final boolean isTurnOff = !config.getPossibleRemoteProperty(RemoteProperty.R_STATS_MODULE_ENABLED,
            guild, Boolean.class);

        return member.getUser().isBot() || isTurnOff || settings.getStatsDisabled();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void onGuildMessageReceived(GuildMessageReceivedEvent event)               { addedMessagesIncCounter(event); }
    @Override public void onSlashCommand(SlashCommandEvent event)                               { slashInteractionsIncCounter(event); }
    @Override public void onGuildMessageUpdate(GuildMessageUpdateEvent event)                   { updatedMessagesIncCounter(event); }
    @Override public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)         { addedReactionsIncCouter(event); }
    @Override public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event)     { updateNickname(event); }
    @Override public void onGuildMemberRemove(GuildMemberRemoveEvent event)                     { deleteMemberStatsAndSettings(event); }
}
