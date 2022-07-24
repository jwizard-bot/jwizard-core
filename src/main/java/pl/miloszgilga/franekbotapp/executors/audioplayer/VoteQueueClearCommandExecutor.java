/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: QueueClearCommandExecutor.java
 * Last modified: 15/07/2022, 06:51
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

package pl.miloszgilga.franekbotapp.executors.audioplayer;

import jdk.jfr.Description;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Queue;

import net.dv8tion.jda.api.entities.VoiceChannel;
import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.audioplayer.PlayerManager;
import pl.miloszgilga.franekbotapp.audioplayer.QueueTrackExtendedInfo;
import pl.miloszgilga.franekbotapp.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.franekbotapp.exceptions.UnableAccessToInvokeCommandException;
import pl.miloszgilga.franekbotapp.executorhandlers.VoteCommandExecutorHandler;

import static pl.miloszgilga.franekbotapp.BotCommand.MUSIC_VOTE_QUEUE_CLEAR;
import static pl.miloszgilga.franekbotapp.executors.audioplayer.VoteSkipTrackCommandExecutor.findVoiceChannelWithBotAndUser;


public class VoteQueueClearCommandExecutor extends Command {

    private final LoggerFactory logger = new LoggerFactory(VoteQueueClearCommandExecutor.class);
    private static final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public VoteQueueClearCommandExecutor() {
        name = MUSIC_VOTE_QUEUE_CLEAR.getCommandName();
        help = MUSIC_VOTE_QUEUE_CLEAR.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]voteclqueue>")
    protected void execute(CommandEvent event) {
        try {
            final Queue<QueueTrackExtendedInfo> queue = playerManager.getMusicManager(event).getScheduler().getQueue();
            if (queue.isEmpty()) {
                throw new EmptyAudioQueueException(event);
            }

            final VoiceChannel voiceChannelWithBot = findVoiceChannelWithBotAndUser(event);
            final var voteHandler = new VoteCommandExecutorHandler(event, voiceChannelWithBot,
                    "kolejka wyczyszczona", "wyczyszczenie kolejki", "kolejka niewyczyszczona");

            if (voteHandler.voteCommandExecutor()) {
                queue.clear();
                logger.info(String.format("Kolejka piosenek w wyniku głosowania użytkowników '%s' została wyczyszczona",
                        voteHandler.allVotedUsers()), event.getGuild());
            }
        } catch (UnableAccessToInvokeCommandException | EmptyAudioQueueException ex) {
            logger.warn(ex.getMessage(), event.getGuild());
        }
    }
}