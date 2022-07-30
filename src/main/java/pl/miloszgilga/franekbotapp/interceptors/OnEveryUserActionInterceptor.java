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

import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

import pl.miloszgilga.franekbotapp.database.HibernateSessionFactory;



public final class OnEveryUserActionInterceptor extends ListenerAdapter {

    private final HibernateSessionFactory sessionFactory = HibernateSessionFactory.getSingletonInstance();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        System.out.println("add new message listener" + event.getMessage() + event.getGuild().getId());
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        System.out.println("update messagage listener" + event.getMessage());
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        System.out.println("delete messagage listener" + event.getMessageId());
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        System.out.println("add reaction listener" + event.getMessageId());
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        System.out.println("remove reaction listener" + event.getMessageId());
    }
}