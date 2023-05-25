/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IMemberStatsRepository.java
 * Last modified: 28/04/2023, 17:07
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

package pl.miloszgilga.domain.member_stats;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import pl.miloszgilga.dto.GuildMembersStatsDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Repository
public interface IMemberStatsRepository extends JpaRepository<MemberStatsEntity, Long> {
    Optional<MemberStatsEntity> findByMember_DiscordIdAndGuild_DiscordId(String memberDiscordId, String guildDiscordId);
    boolean existsByMember_DiscordIdAndGuild_DiscordId(String memberDiscordId, String guildDiscordId);
    void deleteByMember_DiscordIdAndGuild_DiscordId(String memberDiscordId, String guildDiscordId);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Transactional
    @Modifying
    @Query(value = """
        update MemberStatsEntity e set e.messagesSended = e.messagesSended + 1
        where e.member = (select m from MemberEntity m where m.discordId = :memberId)
        and e.guild = (select g from GuildEntity g where g.discordId = :guildId)
    """)
    void increaseSendedMessages(@Param("memberId") String memberId, @Param("guildId") String guildId);

    @Transactional
    @Modifying
    @Query(value = """
        update MemberStatsEntity e set e.messagesUpdated = e.messagesUpdated + 1
        where e.member = (select m from MemberEntity m where m.discordId = :memberId)
        and e.guild = (select g from GuildEntity g where g.discordId = :guildId)
    """)
    void increaseUpdatedMessages(@Param("memberId") String memberId, @Param("guildId") String guildId);

    @Transactional
    @Modifying
    @Query(value = """
        update MemberStatsEntity e set e.reactionsAdded = e.reactionsAdded + 1
        where e.member = (select m from MemberEntity m where m.discordId = :memberId)
        and e.guild = (select g from GuildEntity g where g.discordId = :guildId)
    """)
    void increaseAddedReactions(@Param("memberId") String memberId, @Param("guildId") String guildId);

    @Transactional
    @Modifying
    @Query(value = """
        update MemberStatsEntity e set e.slashInteractions = e.slashInteractions + 1
        where e.member = (select m from MemberEntity m where m.discordId = :memberId)
        and e.guild = (select g from GuildEntity g where g.discordId = :guildId)
    """)
    void increaseSlashInteractions(@Param("memberId") String memberId, @Param("guildId") String guildId);

    @Transactional
    @Modifying
    @Query(value = """
        update MemberStatsEntity e set e.guildNickname = :nickname
        where e.member = (select m from MemberEntity m where m.discordId = :memberId)
        and e.guild = (select g from GuildEntity g where g.discordId = :guildId)
    """)
    void changeNickname(@Param("memberId") String memberId, @Param("guildId") String guildId,
                        @Param("nickname") String nickname);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = """
        select new pl.miloszgilga.dto.GuildMembersStatsDto(
            sum(e.messagesSended), sum(e.messagesUpdated), sum(e.reactionsAdded), sum(e.slashInteractions)
        ) from MemberStatsEntity e join e.guild g where g.discordId = :guildDiscordId group by g
    """)
    GuildMembersStatsDto getAllMemberStats(@Param("guildDiscordId") String guildDiscordId);

    @Transactional
    @Modifying
    @Query(value = """
        update MemberStatsEntity e set e.messagesSended = 0, e.messagesUpdated = 0, e.reactionsAdded = 0,
        e.slashInteractions = 0 where e.guild = (select g from GuildEntity g where g.discordId = :guildId)
    """)
    void resetAllMembersStatsFromGuild(@Param("guildId") String guildId);
}
