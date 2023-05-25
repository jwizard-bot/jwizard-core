/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GuildStatsEntity.java
 * Last modified: 28/04/2023, 17:34
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

package pl.miloszgilga.domain.guild_stats;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.domain.guild.GuildEntity;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@Table(name = "guild_stats")
public class GuildStatsEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "messsages_deleted")             private Long messagesDeleted;
    @Column(name = "reactions_deleted")             private Long reactionsDeleted;

    @OneToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public GuildStatsEntity() { resetStats(); }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Long getMessagesDeleted() {
        return messagesDeleted;
    }

    void setMessagesDeleted(Long messagesDeleted) {
        this.messagesDeleted = messagesDeleted;
    }

    public Long getReactionsDeleted() {
        return reactionsDeleted;
    }

    void setReactionsDeleted(Long reactionsDeleted) {
        this.reactionsDeleted = reactionsDeleted;
    }

    GuildEntity getGuild() {
        return guild;
    }

    public void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void resetStats() {
        messagesDeleted = 0L;
        reactionsDeleted = 0L;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "messagesDeleted=" + messagesDeleted +
            ", reactionsDeleted=" + reactionsDeleted +
            '}';
    }
}
