/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ShowServerStatisticsCommandExecutor.java
 * Last modified: 26/07/2022, 01:44
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

package pl.miloszgilga.franekbotapp.executors.statistics;

import org.hibernate.Session;
import net.dv8tion.jda.api.entities.Member;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.messages.MessageEmbedField;
import pl.miloszgilga.franekbotapp.database.entities.UserStats;
import pl.miloszgilga.franekbotapp.database.HibernateSessionFactory;
import pl.miloszgilga.franekbotapp.database.dao.UserStatsStringifyDAO;

import static pl.miloszgilga.franekbotapp.BotCommand.SERVER_STATS;


public final class ShowServerStatisticsCommandExecutor extends Command {

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final HibernateSessionFactory sessionFactory = HibernateSessionFactory.getSingletonInstance();

    public ShowServerStatisticsCommandExecutor() {
        name = SERVER_STATS.getCommandName();
        help = SERVER_STATS.getCommandDescription();
    }

    @Override
    protected void execute(CommandEvent event) {
        try (Session session = sessionFactory.openTransactionalSessionAndBeginTransaction()) {

            String jpqlQuery = "SELECT u FROM UserStats u WHERE u.serverGuildId=:sid";
            List<UserStats> allUsersStats = session.createQuery(jpqlQuery, UserStats.class)
                    .setParameter("sid", event.getGuild().getId())
                    .getResultList();

            long allAddedMessages = allUsersStats.stream().mapToLong(UserStats::getMessagesSend).sum();
            long allUpdatedMessages = allUsersStats.stream().mapToLong(UserStats::getMessagesUpdated).sum();
            long allAddedReactions = allUsersStats.stream().mapToLong(UserStats::getReactionsAdded).sum();

            long boostingUsers = event.getGuild().getMembers().stream().filter(Member::isBoosting).count();
            long serverBots = event.getGuild().getMembers().stream().filter(u -> u.getUser().isBot()).count();

            final var stats = new UserStatsStringifyDAO(allAddedMessages, allUpdatedMessages, allAddedReactions);
            final List<MessageEmbedField> embedFields = List.of(
                    new MessageEmbedField("Ilość użytkowników:", Integer.toString(event.getGuild().getMemberCount())),
                    new MessageEmbedField("Ilość wspierających:", Long.toString(boostingUsers)),
                    new MessageEmbedField("Ilość botów:", Long.toString(serverBots)),
                    new MessageEmbedField("Wysłane wiadomości:", stats.getMessagesSend()),
                    new MessageEmbedField("Zaktualizowane wiadomości:", stats.getMessagesUpdated()),
                    new MessageEmbedField("Dodane reakcje:", stats.getReactionsAdded())
            );

            final var embedMessage = new EmbedMessage(
                    String.format("Statystyki serwera %s", event.getGuild().getName()),
                    "Poniżej znajdziesz szczegółowe statystyki serwera.", EmbedMessageColor.PURPLE, embedFields
            );
            embedMessage.getBuilder().setFooter(String.format("Statystyki na dzień: %s", formatter.format(new Date())));
            event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
        }
    }
}
