/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IMemberRepository.java
 * Last modified: 28/04/2023, 17:58
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

package pl.miloszgilga.domain.member;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Repository
public interface IMemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByDiscordId(String guildDiscordId);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = """
        from MemberEntity e
        left join fetch e.membersStats left join fetch e.membersSettings
        where e.discordId = :discordId
    """)
    Optional<MemberEntity> findByDiscordIdJoinSettingsAndStats(@Param("discordId") String discordId);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Transactional
    @Modifying
    @Query(value = """
        delete MemberEntity e where e.discordId = :memberId and
        (select count(s1) from e.membersStats s1 join s1.member m1 where m1.discordId = :memberId) = 0 and
        (select count(s2) from e.membersSettings s2 join s2.member m2 where m2.discordId = :memberId) = 0
    """)
    void deleteOrphanMembers(@Param("memberId") String memberId);
}
