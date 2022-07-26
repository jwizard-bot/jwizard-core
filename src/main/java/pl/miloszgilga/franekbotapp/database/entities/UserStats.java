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

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.io.Serializable;


@Entity
public class UserStats implements Serializable {
    private static final long serialversionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String uniqueUserId;
    private String userNameWithId;
    private String serverGuildId;
    private long messagesSend;
    private long messagesUpdated;
    private long messagesDeleted;
    private long reactionsAdded;
    private long reactionsDeleted;

    public UserStats() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getMessagesDeleted() {
        return messagesDeleted;
    }

    public void setMessagesDeleted(long messagesDeleted) {
        this.messagesDeleted = messagesDeleted;
    }

    public long getReactionsAdded() {
        return reactionsAdded;
    }

    public void setReactionsAdded(long reactionsAdded) {
        this.reactionsAdded = reactionsAdded;
    }

    public long getReactionsDeleted() {
        return reactionsDeleted;
    }

    public void setReactionsDeleted(long reactionsDeleted) {
        this.reactionsDeleted = reactionsDeleted;
    }
}