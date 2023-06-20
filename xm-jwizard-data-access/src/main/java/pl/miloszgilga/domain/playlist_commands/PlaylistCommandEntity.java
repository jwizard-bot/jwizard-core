/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlaylistCommandEntity.java
 * Last modified: 20/06/2023, 16:25
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

package pl.miloszgilga.domain.playlist_commands;

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
@Table(name = "playlist_commands")
public class PlaylistCommandEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "addplfile_enabled", insertable = false)         private Boolean addplfileEnabled;
    @Column(name = "addpltrack_enabled", insertable = false)        private Boolean addpltrackEnabled;
    @Column(name = "modplname_enabled", insertable = false)         private Boolean modplnameEnabled;
    @Column(name = "clrplaylist_enabled", insertable = false)       private Boolean clrplaylistEnabled;
    @Column(name = "addplaylist_enabled", insertable = false)       private Boolean addplaylistEnabled;
    @Column(name = "playpl_enabled", insertable = false)            private Boolean playplEnabled;
    @Column(name = "mvtrackpl_enabled", insertable = false)         private Boolean mvtrackplEnabled;
    @Column(name = "rmplaylist_enabled", insertable = false)        private Boolean rmplaylistEnabled;
    @Column(name = "rmtrackpl_enabled", insertable = false)         private Boolean rmtrackplEnabled;
    @Column(name = "savetrackpl_enabled", insertable = false)       private Boolean savetrackplEnabled;
    @Column(name = "plvisiblity_enabled", insertable = false)       private Boolean plvisiblityEnabled;
    @Column(name = "showmempl_enabled", insertable = false)         private Boolean showmemplEnabled;
    @Column(name = "showmypl_enabled", insertable = false)          private Boolean showmyplEnabled;
    @Column(name = "showplsongs_enabled", insertable = false)       private Boolean showplsongsEnabled;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getAddplfileEnabled() {
        return addplfileEnabled;
    }

    public void setAddplfileEnabled(Boolean addplfileEnabled) {
        this.addplfileEnabled = addplfileEnabled;
    }

    public Boolean getAddpltrackEnabled() {
        return addpltrackEnabled;
    }

    public void setAddpltrackEnabled(Boolean addpltrackEnabled) {
        this.addpltrackEnabled = addpltrackEnabled;
    }

    public Boolean getModplnameEnabled() {
        return modplnameEnabled;
    }

    public void setModplnameEnabled(Boolean modplnameEnabled) {
        this.modplnameEnabled = modplnameEnabled;
    }

    public Boolean getClrplaylistEnabled() {
        return clrplaylistEnabled;
    }

    public void setClrplaylistEnabled(Boolean clrplaylistEnabled) {
        this.clrplaylistEnabled = clrplaylistEnabled;
    }

    public Boolean getAddplaylistEnabled() {
        return addplaylistEnabled;
    }

    public void setAddplaylistEnabled(Boolean addplaylistEnabled) {
        this.addplaylistEnabled = addplaylistEnabled;
    }

    public Boolean getPlayplEnabled() {
        return playplEnabled;
    }

    public void setPlayplEnabled(Boolean playplEnabled) {
        this.playplEnabled = playplEnabled;
    }

    public Boolean getMvtrackplEnabled() {
        return mvtrackplEnabled;
    }

    public void setMvtrackplEnabled(Boolean mvtrackplEnabled) {
        this.mvtrackplEnabled = mvtrackplEnabled;
    }

    public Boolean getRmplaylistEnabled() {
        return rmplaylistEnabled;
    }

    public void setRmplaylistEnabled(Boolean rmplaylistEnabled) {
        this.rmplaylistEnabled = rmplaylistEnabled;
    }

    public Boolean getRmtrackplEnabled() {
        return rmtrackplEnabled;
    }

    public void setRmtrackplEnabled(Boolean rmtrackplEnabled) {
        this.rmtrackplEnabled = rmtrackplEnabled;
    }

    public Boolean getSavetrackplEnabled() {
        return savetrackplEnabled;
    }

    public void setSavetrackplEnabled(Boolean savetrackplEnabled) {
        this.savetrackplEnabled = savetrackplEnabled;
    }

    public Boolean getPlvisiblityEnabled() {
        return plvisiblityEnabled;
    }

    public void setPlvisiblityEnabled(Boolean plvisiblityEnabled) {
        this.plvisiblityEnabled = plvisiblityEnabled;
    }

    public Boolean getShowmemplEnabled() {
        return showmemplEnabled;
    }

    public void setShowmemplEnabled(Boolean showmemplEnabled) {
        this.showmemplEnabled = showmemplEnabled;
    }

    public Boolean getShowmyplEnabled() {
        return showmyplEnabled;
    }

    public void setShowmyplEnabled(Boolean showmyplEnabled) {
        this.showmyplEnabled = showmyplEnabled;
    }

    public Boolean getShowplsongsEnabled() {
        return showplsongsEnabled;
    }

    public void setShowplsongsEnabled(Boolean showplsongsEnabled) {
        this.showplsongsEnabled = showplsongsEnabled;
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
            "addplfileEnabled=" + addplfileEnabled +
            ", addpltrackEnabled=" + addpltrackEnabled +
            ", modplnameEnabled=" + modplnameEnabled +
            ", clrplaylistEnabled=" + clrplaylistEnabled +
            ", addplaylistEnabled=" + addplaylistEnabled +
            ", playplEnabled=" + playplEnabled +
            ", mvtrackplEnabled=" + mvtrackplEnabled +
            ", rmplaylistEnabled=" + rmplaylistEnabled +
            ", rmtrackplEnabled=" + rmtrackplEnabled +
            ", savetrackplEnabled=" + savetrackplEnabled +
            ", plvisiblityEnabled=" + plvisiblityEnabled +
            ", showmemplEnabled=" + showmemplEnabled +
            ", showmyplEnabled=" + showmyplEnabled +
            ", showplsongsEnabled=" + showplsongsEnabled +
            '}';
    }
}
