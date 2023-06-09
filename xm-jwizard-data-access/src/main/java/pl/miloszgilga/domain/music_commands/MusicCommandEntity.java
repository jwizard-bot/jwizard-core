/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MusicCommandEntity.java
 * Last modified: 6/8/23, 8:57 PM
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

package pl.miloszgilga.domain.music_commands;

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
@Table(name = "music_commands")
public class MusicCommandEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "play_enabled", insertable = false)          private Boolean playEnabled;
    @Column(name = "pause_enabled", insertable = false)         private Boolean pauseEnabled;
    @Column(name = "resume_enabled", insertable = false)        private Boolean resumeEnabled;
    @Column(name = "repeat_enabled", insertable = false)        private Boolean repeatEnabled;
    @Column(name = "repeatcls_enabled", insertable = false)     private Boolean repeatclsEnabled;
    @Column(name = "loop_enabled", insertable = false)          private Boolean loopEnabled;
    @Column(name = "playing_enabled", insertable = false)       private Boolean playingEnabled;
    @Column(name = "paused_enabled", insertable = false)        private Boolean pausedEnabled;
    @Column(name = "getvolume_enabled", insertable = false)     private Boolean getvolumeEnabled;
    @Column(name = "queue_enabled", insertable = false)         private Boolean queueEnabled;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getPlayEnabled() {
        return playEnabled;
    }

    public void setPlayEnabled(Boolean playEnabled) {
        this.playEnabled = playEnabled;
    }

    public Boolean getPauseEnabled() {
        return pauseEnabled;
    }

    public void setPauseEnabled(Boolean pauseEnabled) {
        this.pauseEnabled = pauseEnabled;
    }

    public Boolean getResumeEnabled() {
        return resumeEnabled;
    }

    public void setResumeEnabled(Boolean resumeEnabled) {
        this.resumeEnabled = resumeEnabled;
    }

    public Boolean getRepeatEnabled() {
        return repeatEnabled;
    }

    public void setRepeatEnabled(Boolean repeatEnabled) {
        this.repeatEnabled = repeatEnabled;
    }

    public Boolean getRepeatclsEnabled() {
        return repeatclsEnabled;
    }

    public void setRepeatclsEnabled(Boolean repeatclsEnabled) {
        this.repeatclsEnabled = repeatclsEnabled;
    }

    public Boolean getLoopEnabled() {
        return loopEnabled;
    }

    public void setLoopEnabled(Boolean loopEnabled) {
        this.loopEnabled = loopEnabled;
    }

    public Boolean getPlayingEnabled() {
        return playingEnabled;
    }

    public void setPlayingEnabled(Boolean playingEnabled) {
        this.playingEnabled = playingEnabled;
    }

    public Boolean getPausedEnabled() {
        return pausedEnabled;
    }

    public void setPausedEnabled(Boolean pausedEnabled) {
        this.pausedEnabled = pausedEnabled;
    }

    public Boolean getGetvolumeEnabled() {
        return getvolumeEnabled;
    }

    public void setGetvolumeEnabled(Boolean getvolumeEnabled) {
        this.getvolumeEnabled = getvolumeEnabled;
    }

    public Boolean getQueueEnabled() {
        return queueEnabled;
    }

    public void setQueueEnabled(Boolean queueEnabled) {
        this.queueEnabled = queueEnabled;
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
            "playEnabled=" + playEnabled +
            ", pauseEnabled=" + pauseEnabled +
            ", resumeEnabled=" + resumeEnabled +
            ", repeatEnabled=" + repeatEnabled +
            ", repeatclsEnabled=" + repeatclsEnabled +
            ", loopEnabled=" + loopEnabled +
            ", playingEnabled=" + playingEnabled +
            ", pausedEnabled=" + pausedEnabled +
            ", getvolumeEnabled=" + getvolumeEnabled +
            ", queueEnabled=" + queueEnabled +
            '}';
    }
}
