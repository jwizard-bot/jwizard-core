/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberSettingsEntity.java
 * Last modified: 09/04/2023, 23:03
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

package pl.miloszgilga.domain.member_settings;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.domain.guild.GuildEntity;
import pl.miloszgilga.domain.member.MemberEntity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.REMOVE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "member_settings")
public class MemberSettingsEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "stats_disabled")            private Boolean statsDisabled;
    @Column(name = "stats_private")             private Boolean statsPrivate;

    @ManyToOne(cascade = { MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private MemberEntity member;

    @ManyToOne(cascade = { MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MemberSettingsEntity(MemberEntity member, GuildEntity guild) {
        this.statsDisabled = false;
        this.statsPrivate = false;
        this.member = member;
        this.guild = guild;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Boolean getStatsDisabled() {
        return statsDisabled;
    }

    public void setStatsDisabled(Boolean statsDisabled) {
        this.statsDisabled = statsDisabled;
    }

    Boolean getStatsPrivate() {
        return statsPrivate;
    }

    public void setStatsPrivate(Boolean statsPrivate) {
        this.statsPrivate = statsPrivate;
    }

    MemberEntity getMember() {
        return member;
    }

    void setMember(MemberEntity member) {
        this.member = member;
    }

    GuildEntity getGuild() {
        return guild;
    }

    void setGuild(GuildEntity guild) {
        this.guild = guild;
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
