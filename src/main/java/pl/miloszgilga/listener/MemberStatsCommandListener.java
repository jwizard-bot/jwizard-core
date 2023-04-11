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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.BotProperty;
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
    private final IMemberSettingsRepository settingsRepository;
    private final IMemberRepository memberRepository;
    private final IGuildRepository guildRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    MemberStatsCommandListener(
        BotConfiguration config, EmbedMessageBuilder embedBuilder, IMemberStatsRepository statsRepository,
        IMemberSettingsRepository settingsRepository, IMemberRepository memberRepository, IGuildRepository guildRepository
    ) {
        super(config, embedBuilder);
        this.statsRepository = statsRepository;
        this.settingsRepository = settingsRepository;
        this.memberRepository = memberRepository;
        this.guildRepository = guildRepository;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addedMessagesIncCounter(GuildMessageReceivedEvent event) {
        if (Objects.isNull(event.getMember())) return;
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        findByMemberAndGuild(event.getMember(), event.getGuild()).ifPresentOrElse(
            memberStats -> {
                memberStats.increaseMessagesSended();
                statsRepository.save(memberStats);
            },
            () -> createMemberStatsIfNotExist(event.getMember(), event.getGuild(),
                MemberStatsEntity::increaseMessagesSended)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updatedMessagesIncCounter(GuildMessageUpdateEvent event) {
        if (Objects.isNull(event.getMember())) return;
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        findByMemberAndGuild(event.getMember(), event.getGuild()).ifPresentOrElse(
            memberStats -> {
                memberStats.increaseMessagesUpdated();
                statsRepository.save(memberStats);
            },
            () -> createMemberStatsIfNotExist(event.getMember(), event.getGuild(),
                MemberStatsEntity::increaseMessagesUpdated)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addedReactionsIncCouter(GuildMessageReactionAddEvent event) {
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        findByMemberAndGuild(event.getMember(), event.getGuild()).ifPresentOrElse(
            memberStats -> {
                memberStats.increaseReactionsAdded();
                statsRepository.save(memberStats);
            },
            () -> createMemberStatsIfNotExist(event.getMember(), event.getGuild(),
                MemberStatsEntity::increaseReactionsAdded)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void slashInteractionsIncCounter(SlashCommandEvent event) {
        if (Objects.isNull(event.getMember()) || Objects.isNull(event.getGuild())) return;
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        findByMemberAndGuild(event.getMember(), event.getGuild()).ifPresentOrElse(
            memberStats -> {
                memberStats.increaseSlashInteractions();
                statsRepository.save(memberStats);
            },
            () -> createMemberStatsIfNotExist(event.getMember(), event.getGuild(),
                MemberStatsEntity::increaseSlashInteractions)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateNickname(GuildMemberUpdateNicknameEvent event) {
        if (checkIfStatsAreDisabled(event.getMember(), event.getGuild())) return;

        findByMemberAndGuild(event.getMember(), event.getGuild()).ifPresentOrElse(
            memberStats -> {
                memberStats.setGuildNickname(event.getNewNickname());
                statsRepository.save(memberStats);
            },
            () -> createMemberStatsIfNotExist(event.getMember(), event.getGuild(), memberStats ->
                memberStats.setGuildNickname(event.getNewNickname()))
        );
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

    private void createMemberStatsIfNotExist(Member member, Guild guild, Consumer<MemberStatsEntity> beforeSave) {
        final Optional<GuildEntity> guildEntity = guildRepository.findByDiscordId(guild.getId());
        if (guildEntity.isEmpty()) return;

        final MemberEntity memberEntity = memberRepository.findByDiscordId(member.getId())
            .orElseGet(() -> memberRepository.save(new MemberEntity(member.getId())));

        final MemberStatsEntity memberStats = new MemberStatsEntity(guildEntity.get(), member, memberEntity);
        final MemberSettingsEntity memberSettings = new MemberSettingsEntity(memberEntity, guildEntity.get());

        beforeSave.accept(memberStats);

        statsRepository.save(memberStats);
        settingsRepository.save(memberSettings);
    }

    private Optional<MemberStatsEntity> findByMemberAndGuild(Member member, Guild guild) {
        return statsRepository.findByMember_DiscordIdAndGuild_DiscordId(member.getId(), guild.getId());
    }

    private boolean checkIfStatsAreDisabled(Member member, Guild guild) {
        final boolean isTurnOff = !config.getProperty(BotProperty.J_STATS_MODULE_ENABLED, Boolean.class);
        final Boolean isPrivate = settingsRepository.isStatsPrivate(member.getId(), guild.getId());
        final Boolean isDisabled = settingsRepository.isStatsDisabled(member.getId(), guild.getId());

        if (Objects.isNull(isPrivate) || Objects.isNull(isDisabled)) return false;
        return member.getUser().isBot() || isTurnOff || isPrivate || isDisabled;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public void onGuildMessageReceived(GuildMessageReceivedEvent event)               { addedMessagesIncCounter(event); }
    @Override public void onSlashCommand(SlashCommandEvent event)                               { slashInteractionsIncCounter(event); }
    @Override public void onGuildMessageUpdate(GuildMessageUpdateEvent event)                   { updatedMessagesIncCounter(event); }
    @Override public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)         { addedReactionsIncCouter(event); }
    @Override public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event)     { updateNickname(event); }
    @Override public void onGuildMemberRemove(GuildMemberRemoveEvent event)                     { deleteMemberStatsAndSettings(event); }
}
