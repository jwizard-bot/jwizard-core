/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IGuildStatsRepository.java
 * Last modified: 28/04/2023, 17:31
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

package pl.miloszgilga.domain.guild_stats;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Repository
public interface IGuildStatsRepository extends JpaRepository<GuildStatsEntity, Long> {
    Optional<GuildStatsEntity> findByGuild_DiscordId(String guildDiscordId);
    boolean existsByGuild_DiscordId(String guildDiscordId);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Transactional
    @Modifying
    @Query(value = """
        update GuildStatsEntity e set e.messagesDeleted = e.messagesDeleted + 1 where
        e.guild = (select g from GuildEntity g where g.discordId = :guildId)
    """)
    void increaseDeletedMessages(@Param("guildId") String guildId);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Transactional
    @Modifying
    @Query(value = """
        update GuildStatsEntity e set e.reactionsDeleted = e.reactionsDeleted + 1 where
        e.guild = (select g from GuildEntity g where g.discordId = :guildId)
    """)
    void increaseDeletedReactions(@Param("guildId") String guildId);
}
