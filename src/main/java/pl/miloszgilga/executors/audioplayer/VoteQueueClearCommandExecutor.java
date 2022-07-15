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

package pl.miloszgilga.executors.audioplayer;

import jdk.jfr.Description;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Queue;

import pl.miloszgilga.audioplayer.PlayerManager;
import net.dv8tion.jda.api.entities.VoiceChannel;
import pl.miloszgilga.audioplayer.QueueTrackExtendedInfo;
import pl.miloszgilga.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.exceptions.UnableAccessToInvokeCommandException;
import pl.miloszgilga.executors.executorhandlers.VoteCommandExecutingHandler;

import static pl.miloszgilga.Command.MUSIC_VOTE_QUEUE_CLEAR;
import static pl.miloszgilga.executors.audioplayer.VoteSkipTrackCommandExecutor.findVoiceChannelWithBotAndUser;


public class VoteQueueClearCommandExecutor extends Command {

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
            final var voteHandler = new VoteCommandExecutingHandler(event, voiceChannelWithBot,
                    "kolejka wyczyszczona", "wyczyszczenie kolejki", "kolejka niewyczyszczona");

            if (voteHandler.voteCommandExecutor()) {
                queue.clear();
            }
        } catch (UnableAccessToInvokeCommandException | EmptyAudioQueueException ex) {
            System.out.println(ex.getMessage());
        }
    }
}