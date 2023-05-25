/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberStatsEntity.java
 * Last modified: 28/04/2023, 17:14
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

package pl.miloszgilga.domain.member_stats;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import net.dv8tion.jda.api.entities.Member;
import org.jmpsl.core.db.AbstractAuditableEntity;

import static jakarta.persistence.FetchType.LAZY;

import pl.miloszgilga.domain.guild.GuildEntity;
import pl.miloszgilga.domain.member.MemberEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "member_stats")
public class MemberStatsEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "guild_nickname")                private String guildNickname;
    @Column(name = "messages_sended")               private Long messagesSended;
    @Column(name = "messages_updated")              private Long messagesUpdated;
    @Column(name = "reactions_added")               private Long reactionsAdded;
    @Column(name = "slash_interactions")            private Long slashInteractions;
    @Column(name = "level")                         private Integer level;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private MemberEntity member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MemberStatsEntity(Member member) {
        resetStats();
        this.guildNickname = Objects.requireNonNullElse(member.getNickname(), member.getUser().getName());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getGuildNickname() {
        return guildNickname;
    }

    void setGuildNickname(String guildNickname) {
        this.guildNickname = guildNickname;
    }

    public Long getMessagesSended() {
        return messagesSended;
    }

    void setMessagesSended(Long messagesSended) {
        this.messagesSended = messagesSended;
    }

    public Long getMessagesUpdated() {
        return messagesUpdated;
    }

    void setMessagesUpdated(Long messagesUpdated) {
        this.messagesUpdated = messagesUpdated;
    }

    public Long getReactionsAdded() {
        return reactionsAdded;
    }

    void setReactionsAdded(Long reactionsAdded) {
        this.reactionsAdded = reactionsAdded;
    }

    MemberEntity getMember() {
        return member;
    }

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    GuildEntity getGuild() {
        return guild;
    }

    public void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    public Long getSlashInteractions() {
        return slashInteractions;
    }

    void setSlashInteractions(Long slashInteractions) {
        this.slashInteractions = slashInteractions;
    }

    public Integer getLevel() {
        return level;
    }

    void setLevel(Integer level) {
        this.level = level;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void resetStats() {
        messagesSended = 0L;
        messagesUpdated = 0L;
        reactionsAdded = 0L;
        level = 0;
        slashInteractions = 0L;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "guildNickname=" + guildNickname +
            ", messagesSended=" + messagesSended +
            ", messagesUpdated=" + messagesUpdated +
            ", reactionsAdded=" + reactionsAdded +
            ", slashInteractions=" + slashInteractions +
            ", level=" + level +
            '}';
    }
}
