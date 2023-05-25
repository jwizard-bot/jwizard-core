/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlaylistTrackEntity.java
 * Last modified: 28/04/2023, 17:34
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

package pl.miloszgilga.domain.playlist_track;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import static jakarta.persistence.FetchType.LAZY;

import pl.miloszgilga.domain.playlist.PlaylistEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "playlist_tracks")
public class PlaylistTrackEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "name")               private String name;
    @Column(name = "query")              private String query;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "playlist_id", referencedColumnName = "id")
    private PlaylistEntity playlist;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getQuery() {
        return query;
    }

    void setQuery(String query) {
        this.query = query;
    }

    PlaylistEntity getPlaylist() {
        return playlist;
    }

    void setPlaylist(PlaylistEntity playlist) {
        this.playlist = playlist;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", query=" + query +
            '}';
    }
}
