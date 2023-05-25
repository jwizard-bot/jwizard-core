/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IGuildRepository.java
 * Last modified: 17/05/2023, 14:53
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

package pl.miloszgilga.domain.guild;

import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Repository
public interface IGuildRepository extends JpaRepository<GuildEntity, Long> {
    Optional<GuildEntity> findByDiscordId(String guildDiscordId);

    @CacheEvict(cacheNames = { "GuildSettingsCache", "GuildModulesCache" }, key = "#p0")
    void deleteGuildEntityByDiscordId(String guildDiscordId);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = """
        from GuildEntity e
        left join fetch e.memberGuildsSettings left join fetch e.memberGuildsStats
        where e.discordId = :discordId
    """)
    Optional<GuildEntity> getGuildByDiscordIdJoinLazy(@Param("discordId") String discordId);

    @Query(value = "select e.discordId from GuildEntity e")
    List<String> findAllGuilds();

    @Query(value = "select count(e.id) > 0 from GuildEntity e where e.discordId = :discordId")
    boolean guildEntityExist(@Param("discordId") String discordId);
}
