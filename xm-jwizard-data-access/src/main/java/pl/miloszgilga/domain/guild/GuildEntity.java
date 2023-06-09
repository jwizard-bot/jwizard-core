/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildEntity.java
 * Last modified: 17/05/2023, 14:48
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

import pl.miloszgilga.domain.vote_commands.VoteCommandEntity;
import pl.miloszgilga.domain.stats_commands.StatsCommandEntity;
import pl.miloszgilga.domain.dj_commands.DjCommandEntity;
import pl.miloszgilga.domain.owner_commands.OwnerCommandEntity;
import pl.miloszgilga.domain.music_commands.MusicCommandEntity;
import pl.miloszgilga.domain.other_commands.OtherCommandEntity;

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

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private VoteCommandEntity voteCommand;

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private StatsCommandEntity statsCommand;

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private DjCommandEntity djCommand;

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private OwnerCommandEntity ownerCommand;

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private MusicCommandEntity musicCommand;

    @OneToOne(cascade = ALL, fetch = LAZY, mappedBy = "guild", orphanRemoval = true)
    private OtherCommandEntity otherCommand;

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

    VoteCommandEntity getVoteCommand() {
        return voteCommand;
    }

    void setVoteCommand(VoteCommandEntity voteCommand) {
        this.voteCommand = voteCommand;
    }

    StatsCommandEntity getStatsCommand() {
        return statsCommand;
    }

    void setStatsCommand(StatsCommandEntity statsCommand) {
        this.statsCommand = statsCommand;
    }

    DjCommandEntity getDjCommand() {
        return djCommand;
    }

    void setDjCommand(DjCommandEntity djCommand) {
        this.djCommand = djCommand;
    }

    OwnerCommandEntity getOwnerCommand() {
        return ownerCommand;
    }

    void setOwnerCommand(OwnerCommandEntity ownerCommand) {
        this.ownerCommand = ownerCommand;
    }

    MusicCommandEntity getMusicCommand() {
        return musicCommand;
    }

    void setMusicCommand(MusicCommandEntity musicCommand) {
        this.musicCommand = musicCommand;
    }

    OtherCommandEntity getOtherCommand() {
        return otherCommand;
    }

    void setOtherCommand(OtherCommandEntity otherCommand) {
        this.otherCommand = otherCommand;
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

    public void persistVoteCommand(VoteCommandEntity voteCommand) {
        this.voteCommand = voteCommand;
        voteCommand.setGuild(this);
    }

    public void persistStatsCommand(StatsCommandEntity statsCommand) {
        this.statsCommand = statsCommand;
        statsCommand.setGuild(this);
    }

    public void persistDjCommand(DjCommandEntity djCommand) {
        this.djCommand = djCommand;
        djCommand.setGuild(this);
    }

    public void persistOwnerCommand(OwnerCommandEntity ownerCommand) {
        this.ownerCommand = ownerCommand;
        ownerCommand.setGuild(this);
    }

    public void persistMusicCommand(MusicCommandEntity musicCommand) {
        this.musicCommand = musicCommand;
        musicCommand.setGuild(this);
    }

    public void persistOtherCommand(OtherCommandEntity otherCommand) {
        this.otherCommand = otherCommand;
        otherCommand.setGuild(this);
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
