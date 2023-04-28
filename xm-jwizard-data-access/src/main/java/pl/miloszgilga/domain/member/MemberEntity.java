/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: Member.java
 * Last modified: 07/04/2023, 01:13
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
