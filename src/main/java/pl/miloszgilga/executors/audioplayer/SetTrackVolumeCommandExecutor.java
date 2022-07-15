/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SetTrackVolumeCommandExecutor.java
 * Last modified: 16/07/2022, 00:37
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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import pl.miloszgilga.messages.EmbedMessage;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.messages.EmbedMessageColor;
import pl.miloszgilga.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.exceptions.IllegalCommandArgumentsException;
import pl.miloszgilga.exceptions.UnableAccessToInvokeCommandException;

import static pl.miloszgilga.FranekBot.config;
import static pl.miloszgilga.Command.MUSIC_VOLUME;
import static pl.miloszgilga.executors.audioplayer.PauseTrackCommandExecutor.checkIfActionEventInvokeBySender;
import static pl.miloszgilga.executors.audioplayer.VoteSkipTrackCommandExecutor.findVoiceChannelWithBotAndUser;


public class SetTrackVolumeCommandExecutor extends Command {

    private static final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public SetTrackVolumeCommandExecutor() {
        name = MUSIC_VOLUME.getCommandName();
        help = MUSIC_VOLUME.getCommandDescription();
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String[] allArgs = event.getArgs().split(" ");
            int volumeLevel;
            try {
                volumeLevel = Integer.parseInt(allArgs[0]);
                if (allArgs.length != 1 || volumeLevel < 0 || volumeLevel > 150) {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException ex) {
                throw new IllegalCommandArgumentsException(event, String.format(
                        "`%s [poziom głośności 0-150]`", config.getDefPrefix() + MUSIC_VOLUME.getCommandName()));
            }

            final MusicManager musicManager = playerManager.getMusicManager(event);
            final AudioPlayer audioPlayer = playerManager.getMusicManager(event).getAudioPlayer();
            if (musicManager.getAudioPlayer().getPlayingTrack() == null) {
                throw new EmptyAudioQueueException(event);
            }

            findVoiceChannelWithBotAndUser(event);
            checkIfActionEventInvokeBySender(event);

            int previousVolume = audioPlayer.getVolume();
            audioPlayer.setVolume(volumeLevel);

            final var embedMessage = new EmbedMessage("", "Zmiana głośności odtwarzacza z **" +
                    previousVolume + "%** na **" + audioPlayer.getVolume() + "%**", EmbedMessageColor.GREEN
            );
            event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();

        } catch (EmptyAudioQueueException | IllegalCommandArgumentsException | UnableAccessToInvokeCommandException ex) {
            System.out.println(ex.getMessage());
        }
    }
}