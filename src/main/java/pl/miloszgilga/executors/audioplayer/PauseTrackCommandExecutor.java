/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: PauseTrackCommandExecutor.java
 * Last modified: 15/07/2022, 18:33
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
import net.dv8tion.jda.api.entities.Member;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.exceptions.UnableAccessToInvokeCommandException;

import static pl.miloszgilga.Command.MUSIC_PAUSE;
import static net.dv8tion.jda.api.Permission.ADMINISTRATOR;


public class PauseTrackCommandExecutor extends Command {

    private static final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public PauseTrackCommandExecutor() {
        name = MUSIC_PAUSE.getCommandName();
        help = MUSIC_PAUSE.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]pause>")
    protected void execute(CommandEvent event) {
        try {
            checkIfActionEventInvokeBySender(event);
            playerManager.getMusicManager(event).getAudioPlayer().setPaused(true);
        } catch (EmptyAudioQueueException | UnableAccessToInvokeCommandException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void checkIfActionEventInvokeBySender(CommandEvent event) {
        final MusicManager musicManager = playerManager.getMusicManager(event);
        final Member senderUserMember = event.getGuild().getMember(event.getAuthor());
        final Member songAddedMember = ((Member)musicManager.getAudioPlayer().getPlayingTrack().getUserData());
        if (musicManager.getAudioPlayer().getPlayingTrack() == null) {
            throw new EmptyAudioQueueException(event);
        }
        if (!songAddedMember.equals(senderUserMember) && !(senderUserMember != null &&
                senderUserMember.getPermissions().contains(ADMINISTRATOR))) {
            throw new UnableAccessToInvokeCommandException(event);
        }
    }
}