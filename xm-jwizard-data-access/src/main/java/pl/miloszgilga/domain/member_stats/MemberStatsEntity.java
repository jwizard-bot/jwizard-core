/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberStats.java
 * Last modified: 07/04/2023, 01:09
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

    public MemberStatsEntity(GuildEntity guildEntity, Member member, MemberEntity memberEntity) {
        resetStats();
        this.guildNickname = Objects.requireNonNullElse(member.getNickname(), member.getUser().getName());
        this.guild = guildEntity;
        this.member = memberEntity;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getGuildNickname() {
        return guildNickname;
    }

    public void setGuildNickname(String guildNickname) {
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

    public void increaseMessagesSended() { messagesSended++; }
    public void increaseMessagesUpdated() { messagesUpdated++; }
    public void increaseReactionsAdded() { reactionsAdded++; }
    public void increaseSlashInteractions() { slashInteractions++; }

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
