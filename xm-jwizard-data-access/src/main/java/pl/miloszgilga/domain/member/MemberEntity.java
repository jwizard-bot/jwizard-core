/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberEntity.java
 * Last modified: 28/04/2023, 17:13
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

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Set;
import java.util.HashSet;
import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.security.user.IAuthUserModel;
import org.jmpsl.security.user.SimpleGrantedRole;
import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.domain.playlist.PlaylistEntity;
import pl.miloszgilga.domain.member_stats.MemberStatsEntity;
import pl.miloszgilga.domain.member_settings.MemberSettingsEntity;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "members")
public class MemberEntity extends AbstractAuditableEntity implements Serializable, IAuthUserModel<SimpleGrantedRole> {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "discord_id")                private String discordId;

    @OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "member", orphanRemoval = true)
    private Set<MemberSettingsEntity> membersSettings;

    @OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "member", orphanRemoval = true)
    private Set<MemberStatsEntity> membersStats;

    @OneToMany(cascade = ALL, fetch = LAZY, mappedBy = "member", orphanRemoval = true)
    private Set<PlaylistEntity> membersPlaylists;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MemberEntity(String discordId) {
        this.discordId = discordId;
        this.membersSettings = new HashSet<>();
        this.membersStats = new HashSet<>();
        this.membersPlaylists = new HashSet<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getDiscordId() {
        return discordId;
    }

    void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    Set<MemberSettingsEntity> getMembersSettings() {
        return membersSettings;
    }

    void setMembersSettings(Set<MemberSettingsEntity> memberSettings) {
        this.membersSettings = memberSettings;
    }

    Set<MemberStatsEntity> getMembersStats() {
        return membersStats;
    }

    void setMembersStats(Set<MemberStatsEntity> memberStats) {
        this.membersStats = memberStats;
    }

    Set<PlaylistEntity> getMembersPlaylists() {
        return membersPlaylists;
    }

    void setMembersPlaylists(Set<PlaylistEntity> memberPlaylists) {
        this.membersPlaylists = memberPlaylists;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addMemberSettings(MemberSettingsEntity memberSettings) {
        membersSettings.add(memberSettings);
        memberSettings.setMember(this);
    }

    public void addMemberStats(MemberStatsEntity memberStats) {
        membersStats.add(memberStats);
        memberStats.setMember(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override public String getAuthUsername()                   { return discordId; }
    @Override public String getAuthPassword()                   { return null; }
    @Override public Set<SimpleGrantedRole> getAuthRoles()      { return SimpleGrantedRole.getSetCollection(); }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "discordId=" + discordId +
            '}';
    }
}
