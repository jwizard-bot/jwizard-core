/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IMemberRepository.java
 * Last modified: 07/04/2023, 01:18
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
