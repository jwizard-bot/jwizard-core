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

package pl.miloszgilga.franekbotapp.executors.audioplayer;

import jdk.jfr.Description;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import pl.miloszgilga.franekbotapp.logger.LoggerFactory;
import pl.miloszgilga.franekbotapp.messages.EmbedMessage;
import pl.miloszgilga.franekbotapp.audioplayer.PlayerManager;
import pl.miloszgilga.franekbotapp.messages.EmbedMessageColor;
import pl.miloszgilga.franekbotapp.audioplayer.TrackScheduler;
import pl.miloszgilga.franekbotapp.exceptions.EmptyAudioQueueException;
import pl.miloszgilga.franekbotapp.exceptions.UserOnVoiceChannelNotFoundException;
import pl.miloszgilga.franekbotapp.exceptions.UnableAccessToInvokeCommandException;

import static pl.miloszgilga.franekbotapp.FranekBot.config;
import static pl.miloszgilga.franekbotapp.Command.MUSIC_LOOP;
import static pl.miloszgilga.franekbotapp.executors.audioplayer.PauseTrackCommandExecutor.checkIfActionEventInvokeBySender;
import static pl.miloszgilga.franekbotapp.executors.audioplayer.VoteSkipTrackCommandExecutor.findVoiceChannelWithBotAndUser;


public class RepeatTrackCommandExecutor extends Command {

    private final LoggerFactory logger = new LoggerFactory(RepeatTrackCommandExecutor.class);
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

            logger.info(String.format("Piosenka '%s' została %s nieskończonej pętli przez '%s'",
                    info.title, embedMessageDescription, event.getAuthor().getAsTag()), event.getGuild());

        } catch (UserOnVoiceChannelNotFoundException | UnableAccessToInvokeCommandException |
                 EmptyAudioQueueException ex) {
            logger.warn(ex.getMessage(), event.getGuild());
        }
    }
}