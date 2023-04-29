/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildEntity.java
 * Last modified: 07/04/2023, 01:12
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

package pl.miloszgilga.domain.guild;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Set;
import java.util.HashSet;
import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.domain.playlist.PlaylistEntity;
import pl.miloszgilga.domain.guild_stats.GuildStatsEntity;
import pl.miloszgilga.domain.guild_modules.GuildModulesEntity;
import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;
import pl.miloszgilga.domain.member_stats.MemberStatsEntity;
import pl.miloszgilga.domain.member_settings.MemberSettingsEntity;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "guilds")
public class GuildEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "name")          private String name;
    @Column(name = "discord_id")    private String discordId;

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private GuildSettingsEntity guildSettings;

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private GuildStatsEntity guildStats;

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private GuildModulesEntity guildModules;

    @OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private Set<MemberStatsEntity> memberGuildsStats = new HashSet<>();

    @OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private Set<MemberSettingsEntity> memberGuildsSettings = new HashSet<>();

    @OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private Set<PlaylistEntity> memberGuildsPlaylists = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public GuildEntity(Guild guild) {
        this.name = guild.getName();
        this.discordId = guild.getId();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getDiscordId() {
        return discordId;
    }

    void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    GuildSettingsEntity getGuildSettings() {
        return guildSettings;
    }

    void setGuildSettings(GuildSettingsEntity guildSettings) {
        this.guildSettings = guildSettings;
    }

    GuildStatsEntity getGuildStats() {
        return guildStats;
    }

    void setGuildStats(GuildStatsEntity guildStats) {
        this.guildStats = guildStats;
    }

    GuildModulesEntity getGuildModules() {
        return guildModules;
    }

    void setGuildModules(GuildModulesEntity guildModules) {
        this.guildModules = guildModules;
    }

    Set<MemberStatsEntity> getMemberGuildsStats() {
        return memberGuildsStats;
    }

    void setMemberGuildsStats(Set<MemberStatsEntity> membersStats) {
        this.memberGuildsStats = membersStats;
    }

    Set<MemberSettingsEntity> getMemberGuildsSettings() {
        return memberGuildsSettings;
    }

    void setMemberGuildsSettings(Set<MemberSettingsEntity> membersSettings) {
        this.memberGuildsSettings = membersSettings;
    }

    Set<PlaylistEntity> getMemberGuildsPlaylists() {
        return memberGuildsPlaylists;
    }

    void setMemberGuildsPlaylists(Set<PlaylistEntity> memberPlaylists) {
        this.memberGuildsPlaylists = memberPlaylists;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void persistGuildStats(GuildStatsEntity guildStats) {
        this.guildStats = guildStats;
        guildStats.setGuild(this);
    }

    public void persistGuildSettings(GuildSettingsEntity guildSettings) {
        this.guildSettings = guildSettings;
        guildSettings.setGuild(this);
    }

    public void persistGuildModules(GuildModulesEntity guildModules) {
        this.guildModules = guildModules;
        guildModules.setGuild(this);
    }

    public void addMemberGuildStats(MemberStatsEntity memberStats) {
        memberGuildsStats.add(memberStats);
        memberStats.setGuild(this);
    }

    public void addMemberGuildSettings(MemberSettingsEntity memberSettings) {
        memberGuildsSettings.add(memberSettings);
        memberSettings.setGuild(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", discordId=" + discordId +
            '}';
    }
}
