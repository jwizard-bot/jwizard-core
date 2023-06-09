/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: DjCommandEntity.java
 * Last modified: 6/8/23, 8:40 PM
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

package pl.miloszgilga.domain.dj_commands;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.domain.guild.GuildEntity;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "dj_commands")
public class DjCommandEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "setvolume_enabled", insertable = false)         private Boolean setvolumeEnabled;
    @Column(name = "volumecls_enabled", insertable = false)         private Boolean volumeclsEnabled;
    @Column(name = "join_enabled", insertable = false)              private Boolean joinEnabled;
    @Column(name = "tracksrm_enabled", insertable = false)          private Boolean tracksrmEnabled;
    @Column(name = "shuffle_enabled", insertable = false)           private Boolean shuffleEnabled;
    @Column(name = "skipto_enabled", insertable = false)            private Boolean skiptoEnabled;
    @Column(name = "skip_enabled", insertable = false)              private Boolean skipEnabled;
    @Column(name = "clear_enabled", insertable = false)             private Boolean clearEnabled;
    @Column(name = "stop_enabled", insertable = false)              private Boolean stopEnabled;
    @Column(name = "move_enabled", insertable = false)              private Boolean moveEnabled;
    @Column(name = "infinite_enabled", insertable = false)          private Boolean infiniteEnabled;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getSetvolumeEnabled() {
        return setvolumeEnabled;
    }

    public void setSetvolumeEnabled(Boolean setvolumeEnabled) {
        this.setvolumeEnabled = setvolumeEnabled;
    }

    public Boolean getVolumeclsEnabled() {
        return volumeclsEnabled;
    }

    public void setVolumeclsEnabled(Boolean volumeclsEnabled) {
        this.volumeclsEnabled = volumeclsEnabled;
    }

    public Boolean getJoinEnabled() {
        return joinEnabled;
    }

    public void setJoinEnabled(Boolean joinEnabled) {
        this.joinEnabled = joinEnabled;
    }

    public Boolean getTracksrmEnabled() {
        return tracksrmEnabled;
    }

    public void setTracksrmEnabled(Boolean tracksrmEnabled) {
        this.tracksrmEnabled = tracksrmEnabled;
    }

    public Boolean getShuffleEnabled() {
        return shuffleEnabled;
    }

    public void setShuffleEnabled(Boolean shuffleEnabled) {
        this.shuffleEnabled = shuffleEnabled;
    }

    public Boolean getSkiptoEnabled() {
        return skiptoEnabled;
    }

    public void setSkiptoEnabled(Boolean skiptoEnabled) {
        this.skiptoEnabled = skiptoEnabled;
    }

    public Boolean getSkipEnabled() {
        return skipEnabled;
    }

    public void setSkipEnabled(Boolean skipEnabled) {
        this.skipEnabled = skipEnabled;
    }

    public Boolean getClearEnabled() {
        return clearEnabled;
    }

    public void setClearEnabled(Boolean clearEnabled) {
        this.clearEnabled = clearEnabled;
    }

    public Boolean getStopEnabled() {
        return stopEnabled;
    }

    public void setStopEnabled(Boolean stopEnabled) {
        this.stopEnabled = stopEnabled;
    }

    public Boolean getMoveEnabled() {
        return moveEnabled;
    }

    public void setMoveEnabled(Boolean moveEnabled) {
        this.moveEnabled = moveEnabled;
    }

    public Boolean getInfiniteEnabled() {
        return infiniteEnabled;
    }

    public void setInfiniteEnabled(Boolean infiniteEnabled) {
        this.infiniteEnabled = infiniteEnabled;
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
            "setvolumeEnabled=" + setvolumeEnabled +
            ", volumeclsEnabled=" + volumeclsEnabled +
            ", joinEnabled=" + joinEnabled +
            ", tracksrmEnabled=" + tracksrmEnabled +
            ", shuffleEnabled=" + shuffleEnabled +
            ", skiptoEnabled=" + skiptoEnabled +
            ", skipEnabled=" + skipEnabled +
            ", clearEnabled=" + clearEnabled +
            ", stopEnabled=" + stopEnabled +
            ", moveEnabled=" + moveEnabled +
            ", infiniteEnabled=" + infiniteEnabled +
            '}';
    }
}
