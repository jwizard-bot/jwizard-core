/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ResumeTrackCommandExecutor.java
 * Last modified: 15/07/2022, 18:34
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

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.audioplayer.MusicManager;
import pl.miloszgilga.franekbotapp.audioplayer.EventWrapper;
import pl.miloszgilga.franekbotapp.audioplayer.PlayerManager;
import pl.miloszgilga.franekbotapp.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.franekbotapp.exceptions.UnableAccessToInvokeCommandException;

import static pl.miloszgilga.franekbotapp.BotCommand.MUSIC_RESUME;
import static pl.miloszgilga.franekbotapp.executors.audioplayer.PauseTrackCommandExecutor.checkIfActionEventInvokeBySender;


public final class ResumeTrackCommandExecutor extends Command {

    private final LoggerFactory logger = new LoggerFactory(ResumeTrackCommandExecutor.class);
    private static final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public ResumeTrackCommandExecutor() {
        name = MUSIC_RESUME.getCommandName();
        help = MUSIC_RESUME.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]resume>")
    protected void execute(CommandEvent event) {
        try {
            checkIfActionEventInvokeBySender(event);
            final MusicManager musicManager = playerManager.getMusicManager(new EventWrapper(event));
            if (musicManager.getScheduler().getPausedTrack() == null) return;

            musicManager.getAudioPlayer().setPaused(false);
        } catch (EmptyAudioQueueException | UnableAccessToInvokeCommandException ex) {
            logger.warn(ex.getMessage(), event.getGuild());
        }
    }
}