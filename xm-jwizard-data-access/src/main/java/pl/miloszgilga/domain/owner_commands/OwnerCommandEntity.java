/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ModulesCommandEntity.java
 * Last modified: 6/8/23, 8:48 PM
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

package pl.miloszgilga.domain.owner_commands;

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
@Table(name = "owner_commands")
public class OwnerCommandEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "onstatsm_enabled", insertable = false)      private Boolean onstatsmEnabled;
    @Column(name = "offstatsm_enabled", insertable = false)     private Boolean offstatsmEnabled;
    @Column(name = "onmusicm_enabled", insertable = false)      private Boolean onmusicmEnabled;
    @Column(name = "offmusicm_enabled", insertable = false)     private Boolean offmusicmEnabled;
    @Column(name = "onplaylm_enabled", insertable = false)      private Boolean onplaylmEnabled;
    @Column(name = "offplaylm_enabled", insertable = false)     private Boolean offplaylmEnabled;
    @Column(name = "onvotingm_enabled", insertable = false)     private Boolean onvotingmEnabled;
    @Column(name = "offvotingm_enabled", insertable = false)    private Boolean offvotingmEnabled;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getOnstatsmEnabled() {
        return onstatsmEnabled;
    }

    public void setOnstatsmEnabled(Boolean onstatsmEnabled) {
        this.onstatsmEnabled = onstatsmEnabled;
    }

    public Boolean getOffstatsmEnabled() {
        return offstatsmEnabled;
    }

    public void setOffstatsmEnabled(Boolean offstatsmEnabled) {
        this.offstatsmEnabled = offstatsmEnabled;
    }

    public Boolean getOnmusicmEnabled() {
        return onmusicmEnabled;
    }

    public void setOnmusicmEnabled(Boolean onmusicmEnabled) {
        this.onmusicmEnabled = onmusicmEnabled;
    }

    public Boolean getOffmusicmEnabled() {
        return offmusicmEnabled;
    }

    public void setOffmusicmEnabled(Boolean offmusicmEnabled) {
        this.offmusicmEnabled = offmusicmEnabled;
    }

    public Boolean getOnplaylmEnabled() {
        return onplaylmEnabled;
    }

    public void setOnplaylmEnabled(Boolean onplaylmEnabled) {
        this.onplaylmEnabled = onplaylmEnabled;
    }

    public Boolean getOffplaylmEnabled() {
        return offplaylmEnabled;
    }

    public void setOffplaylmEnabled(Boolean offplaylmEnabled) {
        this.offplaylmEnabled = offplaylmEnabled;
    }

    public Boolean getOnvotingmEnabled() {
        return onvotingmEnabled;
    }

    public void setOnvotingmEnabled(Boolean onvotingmEnabled) {
        this.onvotingmEnabled = onvotingmEnabled;
    }

    public Boolean getOffvotingmEnabled() {
        return offvotingmEnabled;
    }

    public void setOffvotingmEnabled(Boolean offvotingmEnabled) {
        this.offvotingmEnabled = offvotingmEnabled;
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
            "onstatsmEnabled=" + onstatsmEnabled +
            ", offstatsmEnabled=" + offstatsmEnabled +
            ", onmusicmEnabled=" + onmusicmEnabled +
            ", offmusicmEnabled=" + offmusicmEnabled +
            ", onplaylmEnabled=" + onplaylmEnabled +
            ", offplaylmEnabled=" + offplaylmEnabled +
            ", onvotingmEnabled=" + onvotingmEnabled +
            ", offvotingmEnabled=" + offvotingmEnabled +
            '}';
    }
}
