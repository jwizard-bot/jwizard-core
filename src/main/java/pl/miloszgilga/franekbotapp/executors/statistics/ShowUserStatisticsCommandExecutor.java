/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ShowUserStatisticsCommandExecutor.java
 * Last modified: 26/07/2022, 01:48
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
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;
import jakarta.persistence.NoResultException;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.List;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.database.dto.UserStatsDto;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.messages.MessageEmbedField;
import pl.miloszgilga.franekbotapp.database.HibernateSessionFactory;
import pl.miloszgilga.franekbotapp.exceptions.UserHasNotStatisticsYetException;
import pl.miloszgilga.franekbotapp.exceptions.IllegalCommandArgumentsException;
import pl.miloszgilga.franekbotapp.exceptions.FindingUserByGuidNotFoundException;

import static pl.miloszgilga.franekbotapp.BotCommand.USER_STATS;
import static pl.miloszgilga.franekbotapp.configuration.ConfigurationLoader.config;


public final class ShowUserStatisticsCommandExecutor extends Command {

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final LoggerFactory logger = new LoggerFactory(ShowUserStatisticsCommandExecutor.class);
    private final HibernateSessionFactory sessionFactory = HibernateSessionFactory.getSingletonInstance();
    private final Pattern pattern = Pattern.compile("^<@.[0-9]+.>$");

    public ShowUserStatisticsCommandExecutor() {
        name = USER_STATS.getCommandName();
        help = USER_STATS.getCommandDescription();
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            User findingUserStats = findUserBaseArgs(event);
            try {
                try (Session session = sessionFactory.openTransactionalSessionAndBeginTransaction()) {
                    final String jpqlQuery =
                            "SELECT new pl.miloszgilga.franekbotapp.database.dto.UserStatsDto(" +
                            "u.id, " +
                            "u.messagesSend, " +
                            "u.messagesUpdated, " +
                            "u.reactionsAdded) " +
                            "FROM UserStats u WHERE u.serverGuildId=:sid AND u.uniqueUserId=:uid";
                    final UserStatsDto userStats = session.createQuery(jpqlQuery, UserStatsDto.class)
                            .setParameter("sid", event.getGuild().getId())
                            .setParameter("uid", findingUserStats.getId())
                            .getSingleResult();
                    final var embedMessage = new EmbedMessage(
                            String.format("Statystyki użytkownika %s", userStats.getUserId()),
                            "Poniżej znajdziesz szczegółowe statystyki wybranego użytkownika.",
                            EmbedMessageColor.PURPLE,
                            allUpdatableEmbededMessages(userStats)
                    );
                    embedMessage.getBuilder().setFooter(
                            String.format("Statystyki na dzień: %s", formatter.format(new Date())),
                            findingUserStats.getAvatarUrl());
                    event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();
                }
            } catch (NoResultException ex) {
                throw new UserHasNotStatisticsYetException(event, findingUserStats);
            }
        } catch (IllegalCommandArgumentsException | FindingUserByGuidNotFoundException ex) {
            logger.error(event.getGuild(), ex.getMessage());
        } catch (UserHasNotStatisticsYetException ex) {
            logger.warn(event.getGuild(), ex.getMessage());
        }
    }

    private User findUserBaseArgs(CommandEvent event) {
        final Matcher matcher = pattern.matcher(event.getArgs());
        if (!matcher.matches()) throw new IllegalCommandArgumentsException(event, USER_STATS, String.format(
                "`%s [opcjonalny tag użytkownika]`", config.getPrefix() + USER_STATS.getCommandName()));
        if (event.getArgs().length() == 0) return event.getAuthor();

        Member userMember = event.getGuild().getMemberById(event.getArgs().replaceAll("[^\\d.]", ""));
        if (userMember == null) throw new FindingUserByGuidNotFoundException(event, event.getArgs());
        return userMember.getUser();
    }

    private List<MessageEmbedField> allUpdatableEmbededMessages(UserStatsDto userStats) {
        return List.of(
                new MessageEmbedField("Wysłane wiadomości:", userStats.getAddedMess()),
                new MessageEmbedField("Zaktualizowane wiadomości:", userStats.getUpdatedMess()),
                new MessageEmbedField("Dodane reakcje:", userStats.getAddedReacts())
        );
    }
}
