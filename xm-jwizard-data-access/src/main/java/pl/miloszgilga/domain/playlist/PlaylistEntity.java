/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlaylistEntity.java
 * Last modified: 28/04/2023, 17:35
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

package pl.miloszgilga.domain.playlist;

import jakarta.persistence.*;

import java.util.Set;
import java.util.HashSet;
import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

import pl.miloszgilga.domain.guild.GuildEntity;
import pl.miloszgilga.domain.member.MemberEntity;
import pl.miloszgilga.domain.playlist_track.PlaylistTrackEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@Table(name = "playlists")
public class PlaylistEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "name")                  private String name;
    @Column(name = "is_private")            private Boolean isPrivate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private MemberEntity member;

    @OneToMany(cascade = ALL, mappedBy = "playlist", orphanRemoval = true)
    private Set<PlaylistTrackEntity> tracks;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public PlaylistEntity() {
        this.tracks = new HashSet<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    Boolean getPrivate() {
        return isPrivate;
    }

    void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    MemberEntity getMember() {
        return member;
    }

    void setMember(MemberEntity member) {
        this.member = member;
    }

    GuildEntity getGuild() {
        return guild;
    }

    void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    Set<PlaylistTrackEntity> getTracks() {
        return tracks;
    }

    void setTracks(Set<PlaylistTrackEntity> tracks) {
        this.tracks = tracks;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", isPrivate=" + isPrivate +
            '}';
    }
}
