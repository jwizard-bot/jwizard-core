/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberSettingsEntity.java
 * Last modified: 28/04/2023, 17:51
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

package pl.miloszgilga.domain.member_settings;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.domain.guild.GuildEntity;
import pl.miloszgilga.domain.member.MemberEntity;

import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@Table(name = "member_settings")
public class MemberSettingsEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "stats_disabled")            private Boolean statsDisabled;
    @Column(name = "stats_private")             private Boolean statsPrivate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private MemberEntity member;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MemberSettingsEntity() {
        this.statsDisabled = false;
        this.statsPrivate = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean getStatsDisabled() {
        return statsDisabled;
    }

    public void setStatsDisabled(Boolean statsDisabled) {
        this.statsDisabled = statsDisabled;
    }

    public Boolean getStatsPrivate() {
        return statsPrivate;
    }

    public void setStatsPrivate(Boolean statsPrivate) {
        this.statsPrivate = statsPrivate;
    }

    GuildEntity getGuild() {
        return guild;
    }

    public void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    MemberEntity getMember() {
        return member;
    }

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "statsDisabled=" + statsDisabled +
            ", statsPrivate=" + statsPrivate +
            '}';
    }
}
