/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildModulesEntity.java
 * Last modified: 28/04/2023, 19:44
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
