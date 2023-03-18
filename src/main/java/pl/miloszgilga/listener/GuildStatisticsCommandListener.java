/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: UserStatisticsCommandListener.java
 * Last modified: 18/03/2023, 11:34
 * Project name: jwizard-discord-bot
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

package pl.miloszgilga.listener;

import org.hibernate.Session;

import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import java.util.function.Consumer;

import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.db.HibernateFactory;
import pl.miloszgilga.core.AbstractListenerAdapter;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableListenerLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableListenerLazyService
public class GuildStatisticsCommandListener extends AbstractListenerAdapter {

    private final HibernateFactory hibernate;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    GuildStatisticsCommandListener(HibernateFactory hibernate, BotConfiguration config, EmbedMessageBuilder embedBuilder) {
        super(config, embedBuilder);
        this.hibernate = hibernate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        final Consumer<Session> onExecute = session -> {

        };
        final Consumer<RuntimeException> onException = ex -> {

        };
        hibernate.executeTrasactQuery(onExecute, onException);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        final Consumer<Session> onExecute = session -> {

        };
        final Consumer<RuntimeException> onException = ex -> {

        };
        hibernate.executeTrasactQuery(onExecute, onException);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        final Consumer<Session> onExecute = session -> {

        };
        final Consumer<RuntimeException> onException = ex -> {

        };
        hibernate.executeTrasactQuery(onExecute, onException);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        final Consumer<Session> onExecute = session -> {

        };
        final Consumer<RuntimeException> onException = ex -> {

        };
        hibernate.executeTrasactQuery(onExecute, onException);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        final Consumer<Session> onExecute = session -> {

        };
        final Consumer<RuntimeException> onException = ex -> {

        };
        hibernate.executeTrasactQuery(onExecute, onException);
    }
}
