/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: VoteCommandEntity.java
 * Last modified: 6/8/23, 8:13 PM
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

package pl.miloszgilga.domain.vote_commands;

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
@Table(name = "vote_commands")
public class VoteCommandEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "vshuffle_enabled", insertable = false)  private Boolean vshuffleEnabled;
    @Column(name = "vskip_enabled", insertable = false)     private Boolean vskipEnabled;
    @Column(name = "vskipto_enabled", insertable = false)   private Boolean vskiptoEnabled;
    @Column(name = "vclear_enabled", insertable = false)    private Boolean vclearEnabled;
    @Column(name = "vstop_enabled", insertable = false)     private Boolean vstopEnabled;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getVshuffleEnabled() {
        return vshuffleEnabled;
    }

    public void setVshuffleEnabled(Boolean vshuffleEnabled) {
        this.vshuffleEnabled = vshuffleEnabled;
    }

    public Boolean getVskipEnabled() {
        return vskipEnabled;
    }

    public void setVskipEnabled(Boolean vskipEnabled) {
        this.vskipEnabled = vskipEnabled;
    }

    public Boolean getVskiptoEnabled() {
        return vskiptoEnabled;
    }

    public void setVskiptoEnabled(Boolean vskiptoEnabled) {
        this.vskiptoEnabled = vskiptoEnabled;
    }

    public Boolean getVclearEnabled() {
        return vclearEnabled;
    }

    public void setVclearEnabled(Boolean vclearEnabled) {
        this.vclearEnabled = vclearEnabled;
    }

    public Boolean getVstopEnabled() {
        return vstopEnabled;
    }

    public void setVstopEnabled(Boolean vstopEnabled) {
        this.vstopEnabled = vstopEnabled;
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
            "vshuffleEnabled=" + vshuffleEnabled +
            ", vskipEnabled=" + vskipEnabled +
            ", vskiptoEnabled=" + vskiptoEnabled +
            ", vclearEnabled=" + vclearEnabled +
            ", vstopEnabled=" + vstopEnabled +
            '}';
    }
}
