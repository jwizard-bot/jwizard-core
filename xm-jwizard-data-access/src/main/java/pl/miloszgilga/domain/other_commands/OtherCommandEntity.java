/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: OtherCommandEntity.java
 * Last modified: 6/8/23, 9:02 PM
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

package pl.miloszgilga.domain.other_commands;

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
@Table(name = "other_commands")
public class OtherCommandEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "help_enabled", insertable = false)              private Boolean helpEnabled;
    @Column(name = "helpme_enabled", insertable = false)            private Boolean helpmeEnabled;
    @Column(name = "debug_enabled", insertable = false)             private Boolean debugEnabled;
    @Column(name = "setaudiochn_enabled", insertable = false)       private Boolean setaudiochnEnabled;
    @Column(name = "setdjrole_enabled", insertable = false)         private Boolean setdjroleEnabled;
    @Column(name = "setlang_enabled", insertable = false)           private Boolean setlangEnabled;
    @Column(name = "settrackrep_enabled", insertable = false)       private Boolean settrackrepEnabled;
    @Column(name = "setdefvol_enabled", insertable = false)         private Boolean setdefvolEnabled;
    @Column(name = "setskratio_enabled", insertable = false)        private Boolean setskratioEnabled;
    @Column(name = "settimevot_enabled", insertable = false)        private Boolean settimevotEnabled;
    @Column(name = "settleavem_enabled", insertable = false)        private Boolean settleavemEnabled;
    @Column(name = "settleavetr_enabled", insertable = false)       private Boolean settleavetrEnabled;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getHelpEnabled() {
        return helpEnabled;
    }

    public void setHelpEnabled(Boolean helpEnabled) {
        this.helpEnabled = helpEnabled;
    }

    public Boolean getHelpmeEnabled() {
        return helpmeEnabled;
    }

    public void setHelpmeEnabled(Boolean helpmeEnabled) {
        this.helpmeEnabled = helpmeEnabled;
    }

    public Boolean getDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(Boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public Boolean getSetaudiochnEnabled() {
        return setaudiochnEnabled;
    }

    public void setSetaudiochnEnabled(Boolean setaudiochnEnabled) {
        this.setaudiochnEnabled = setaudiochnEnabled;
    }

    public Boolean getSetdjroleEnabled() {
        return setdjroleEnabled;
    }

    public void setSetdjroleEnabled(Boolean setdjroleEnabled) {
        this.setdjroleEnabled = setdjroleEnabled;
    }

    public Boolean getSetlangEnabled() {
        return setlangEnabled;
    }

    public void setSetlangEnabled(Boolean setlangEnabled) {
        this.setlangEnabled = setlangEnabled;
    }

    public Boolean getSettrackrepEnabled() {
        return settrackrepEnabled;
    }

    public void setSettrackrepEnabled(Boolean settrackrepEnabled) {
        this.settrackrepEnabled = settrackrepEnabled;
    }

    public Boolean getSetdefvolEnabled() {
        return setdefvolEnabled;
    }

    public void setSetdefvolEnabled(Boolean setdefvolEnabled) {
        this.setdefvolEnabled = setdefvolEnabled;
    }

    public Boolean getSetskratioEnabled() {
        return setskratioEnabled;
    }

    public void setSetskratioEnabled(Boolean setskratioEnabled) {
        this.setskratioEnabled = setskratioEnabled;
    }

    public Boolean getSettimevotEnabled() {
        return settimevotEnabled;
    }

    public void setSettimevotEnabled(Boolean settimevotEnabled) {
        this.settimevotEnabled = settimevotEnabled;
    }

    public Boolean getSettleavemEnabled() {
        return settleavemEnabled;
    }

    public void setSettleavemEnabled(Boolean settleavemEnabled) {
        this.settleavemEnabled = settleavemEnabled;
    }

    public Boolean getSettleavetrEnabled() {
        return settleavetrEnabled;
    }

    public void setSettleavetrEnabled(Boolean settleavetrEnabled) {
        this.settleavetrEnabled = settleavetrEnabled;
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
            "helpEnabled=" + helpEnabled +
            ", helpmeEnabled=" + helpmeEnabled +
            ", debugEnabled=" + debugEnabled +
            ", setaudiochnEnabled=" + setaudiochnEnabled +
            ", setdjroleEnabled=" + setdjroleEnabled +
            ", setlangEnabled=" + setlangEnabled +
            ", settrackrepEnabled=" + settrackrepEnabled +
            ", setdefvolEnabled=" + setdefvolEnabled +
            ", setskratioEnabled=" + setskratioEnabled +
            ", settimevotEnabled=" + settimevotEnabled +
            ", settleavemEnabled=" + settleavemEnabled +
            ", settleavetrEnabled=" + settleavetrEnabled +
            '}';
    }
}
