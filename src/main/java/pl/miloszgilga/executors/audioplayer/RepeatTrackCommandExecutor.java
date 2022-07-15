/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioRepeatLoopCommandExecutor.java
 * Last modified: 15/07/2022, 02:30
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

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.messages.EmbedMessageColor;
import pl.miloszgilga.audioplayer.TrackScheduler;
import pl.miloszgilga.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.exceptions.UserOnVoiceChannelNotFoundException;
import pl.miloszgilga.exceptions.UnableAccessToInvokeCommandException;

import static pl.miloszgilga.FranekBot.config;
import static pl.miloszgilga.Command.MUSIC_LOOP;
import static pl.miloszgilga.executors.audioplayer.PauseTrackCommandExecutor.checkIfActionEventInvokeBySender;
import static pl.miloszgilga.executors.audioplayer.VoteSkipTrackCommandExecutor.findVoiceChannelWithBotAndUser;


public class RepeatTrackCommandExecutor extends Command {

    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public RepeatTrackCommandExecutor() {
        name = MUSIC_LOOP.getCommandName();
        help = MUSIC_LOOP.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]loop>")
    protected void execute(CommandEvent event) {
        try {
            final TrackScheduler trackScheduler = playerManager.getMusicManager(event).getScheduler();
            if (trackScheduler.getAudioPlayer().getPlayingTrack() == null) {
                throw new EmptyAudioQueueException(event);
            }

            findVoiceChannelWithBotAndUser(event);
            checkIfActionEventInvokeBySender(event);
            trackScheduler.setRepeating(!trackScheduler.isRepeating());

            final AudioTrackInfo info = trackScheduler.getAudioPlayer().getPlayingTrack().getInfo();
            String embedMessageDescription = "usunięta z";
            String embedMessageRemoveLoop = "";
            if (trackScheduler.isRepeating()) {
                embedMessageDescription = "umieszczona w";
                embedMessageRemoveLoop = String.format("Aby usunąć piosenkę z pętli, wpisz ponownie komendę `%s%s`.",
                        config.getDefPrefix(), MUSIC_LOOP.getCommandName());
            }

            final var embedMessage = new EmbedMessage("", String.format(
                    "Piosenka **%s: %s** została %s nieskończonej pętli. %s",
                    info.author, info.title, embedMessageDescription, embedMessageRemoveLoop),
                    EmbedMessageColor.GREEN);
            event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();

        } catch (UserOnVoiceChannelNotFoundException | UnableAccessToInvokeCommandException |
                 EmptyAudioQueueException ex) {
            System.out.println(ex.getMessage());
        }
    }
}