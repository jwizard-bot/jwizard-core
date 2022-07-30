/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ServerEntity.java
 * Last modified: 25/07/2022, 23:29
 * Project name: franek-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.franekbotapp.database.entities;

import lombok.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

import java.io.Serializable;


@Entity
@ToString
@Table(name = "USER_STATS")
public class UserStats extends AuditEntity implements Serializable, BasicHibernateEntity {
    private static final long serialversionUID = 1L;

    @Column(name = "UNIQUE_USER_ID")    private String uniqueUserId;
    @Column(name = "USER_NAME_WITH_ID") private String userNameWithId;
    @Column(name = "SERVER_GUILD_ID")   private String serverGuildId;
    @Column(name = "MESSAGES_SEND")     private long messagesSend;
    @Column(name = "MESSAGES_UPDATED")  private long messagesUpdated;
    @Column(name = "REACTIONS_ADDED")   private long reactionsAdded;

    public UserStats() { }

    public UserStats(String uniqueUserId, String userNameWithId, String serverGuildId) {
        this.uniqueUserId = uniqueUserId;
        this.userNameWithId = userNameWithId;
        this.serverGuildId = serverGuildId;
    }

    public String getUniqueUserId() {
        return uniqueUserId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;
    }

    public String getUserNameWithId() {
        return userNameWithId;
    }

    public void setUserNameWithId(String userNameWithId) {
        this.userNameWithId = userNameWithId;
    }

    public String getServerGuildId() {
        return serverGuildId;
    }

    public void setServerGuildId(String serverGuildId) {
        this.serverGuildId = serverGuildId;
    }

    public long getMessagesSend() {
        return messagesSend;
    }

    public void setMessagesSend(long messagesSend) {
        this.messagesSend = messagesSend;
    }

    public long getMessagesUpdated() {
        return messagesUpdated;
    }

    public void setMessagesUpdated(long messagesUpdated) {
        this.messagesUpdated = messagesUpdated;
    }

    public long getReactionsAdded() {
        return reactionsAdded;
    }

    public void setReactionsAdded(long reactionsAdded) {
        this.reactionsAdded = reactionsAdded;
    }
}