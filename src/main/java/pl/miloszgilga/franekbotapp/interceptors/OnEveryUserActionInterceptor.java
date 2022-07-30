/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OnEveryMessageSendInterceptor.java
 * Last modified: 26/07/2022, 01:51
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

package pl.miloszgilga.franekbotapp.interceptors;

import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import jakarta.persistence.NoResultException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import pl.miloszgilga.franekbotapp.database.HibernateSessionFactory;
import pl.miloszgilga.franekbotapp.database.entities.UserStats;

import static pl.miloszgilga.franekbotapp.interceptors.UpdateParameters.*;


public final class OnEveryUserActionInterceptor extends ListenerAdapter {

    private final HibernateSessionFactory sessionFactory = HibernateSessionFactory.getSingletonInstance();

    @Override
    public void onMessageReceived(@NotNull final MessageReceivedEvent event) {
        findUserAndUpdateSelectedParameter(event.getAuthor(), event.getGuild(), MESSAGES_CREATED);
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        findUserAndUpdateSelectedParameter(event.getAuthor(), event.getGuild(), MESSAGES_UPDATED);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser() == null) return;
        findUserAndUpdateSelectedParameter(event.getUser(), event.getGuild(), REACTIONS_ADDED);
    }

    private void findUserAndUpdateSelectedParameter(User user, Guild guild, UpdateParameters parameters) {
        try (Session session = sessionFactory.openTransactionalSessionAndBeginTransaction()) {
            try {
                UserStats userStats = session
                        .createQuery("SELECT u FROM UserStats u WHERE u.uniqueUserId=:uid AND u.serverGuildId=:sid",
                                UserStats.class)
                        .setParameter("uid", user.getId()).setParameter("sid", guild.getId()).getSingleResult();
                updateUserStats(parameters, userStats);
                session.flush();
            } catch (NoResultException ex) {
                final var userStats = new UserStats(user.getId(), user.getAsTag(), guild.getId());
                updateUserStats(parameters, userStats);
                session.persist(userStats);
            }
        }
    }

    private void updateUserStats(UpdateParameters parameters, UserStats userStats) {
        switch (parameters) {
            case MESSAGES_CREATED:
                userStats.setMessagesSend(userStats.getMessagesSend() + 1);
                break;
            case MESSAGES_UPDATED:
                userStats.setMessagesUpdated(userStats.getMessagesUpdated() + 1);
                break;
            case REACTIONS_ADDED:
                userStats.setReactionsAdded(userStats.getReactionsAdded() + 1);
        }
    }
}