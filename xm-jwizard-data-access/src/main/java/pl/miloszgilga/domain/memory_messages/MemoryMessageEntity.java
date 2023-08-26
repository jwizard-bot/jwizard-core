/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemoryMessageEntity.java
 * Last modified: 19/06/2023, 16:13
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

package pl.miloszgilga.domain.memory_messages;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import pl.miloszgilga.domain.guild.GuildEntity;

import org.jmpsl.core.db.AbstractAuditableEntity;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.EnumType.STRING;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "memory_messages")
public class MemoryMessageEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "message_id")                    private String messageId;
    @Column(name = "channel_id")                    private String channelId;
    @Column(name = "type") @Enumerated(STRING)      private MessageType messageType;

    @ManyToOne(cascade = { PERSIST, MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guild;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MemoryMessageEntity(String messageId, String channelId, MessageType type) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.messageType = type;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getMessageId() {
        return messageId;
    }

    void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChannelId() {
        return channelId;
    }

    void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    void setMessageType(MessageType messageType) {
        this.messageType = messageType;
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
            "messageId=" + messageId +
            ", channelId=" + channelId +
            ", messageType=" + messageType +
            '}';
    }
}
