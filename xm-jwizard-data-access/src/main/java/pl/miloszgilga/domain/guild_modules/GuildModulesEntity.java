/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildModulesEntity.java
 * Last modified: 28/04/2023, 21:32
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

package pl.miloszgilga.domain.guild_modules;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.domain.guild.GuildEntity;

import java.io.Serial;
import java.io.Serializable;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "guild_modules")
public class GuildModulesEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "stats_module_enabled")              private Boolean statsModuleEnabled;
    @Column(name = "music_module_enabled")              private Boolean musicModuleEnabled;
    @Column(name = "playlists_module_enabled")          private Boolean playlistsModuleEnabled;
    @Column(name = "voting_module_enabled")             private Boolean votingModuleEnabled;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getStatsModuleEnabled() {
        return statsModuleEnabled;
    }

    public void setStatsModuleEnabled(Boolean statsModuleEnabled) {
        this.statsModuleEnabled = statsModuleEnabled;
    }

    public Boolean getMusicModuleEnabled() {
        return musicModuleEnabled;
    }

    public void setMusicModuleEnabled(Boolean musicModuleEnabled) {
        this.musicModuleEnabled = musicModuleEnabled;
    }

    public Boolean getPlaylistsModuleEnabled() {
        return playlistsModuleEnabled;
    }

    public void setPlaylistsModuleEnabled(Boolean playlistsModuleEnabled) {
        this.playlistsModuleEnabled = playlistsModuleEnabled;
    }

    public Boolean getVotingModuleEnabled() {
        return votingModuleEnabled;
    }

    public void setVotingModuleEnabled(Boolean votingModuleEnabled) {
        this.votingModuleEnabled = votingModuleEnabled;
    }

    GuildEntity getGuild() {
        return guild;
    }

    public void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "statsModuleEnabled=" + statsModuleEnabled +
            ", musicModuleEnabled=" + musicModuleEnabled +
            ", playlistsModuleEnabled=" + playlistsModuleEnabled +
            ", votingModuleEnabled=" + votingModuleEnabled +
            '}';
    }
}
