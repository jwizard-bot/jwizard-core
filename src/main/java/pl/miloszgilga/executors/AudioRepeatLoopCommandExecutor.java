/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioRepeatInfiniteLoopCommandExecutor.java
 * Last modified: 14/07/2022, 21:04
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

package pl.miloszgilga.executors;

import jdk.jfr.Description;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.Locale;

import pl.miloszgilga.messages.EmbedMessage;
import static pl.miloszgilga.FranekBot.config;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.messages.EmbedMessageColor;
import static pl.miloszgilga.Command.MUSIC_LOOP;
import pl.miloszgilga.audioplayer.TrackScheduler;
import pl.miloszgilga.exceptions.EmptyAudioQueueException;


public class AudioRepeatLoopCommandExecutor extends Command {

    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public AudioRepeatLoopCommandExecutor() {
        name = MUSIC_LOOP.getCommandName();
        help = MUSIC_LOOP.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]loop>")
    protected void execute(CommandEvent event) {
        try {
            final TrackScheduler trackScheduler = playerManager.getMusicManager(event.getGuild()).getScheduler();
            if (trackScheduler.getAudioPlayer().getPlayingTrack() == null) {
                throw new EmptyAudioQueueException(event);
            }
            trackScheduler.setRepeating(!trackScheduler.isRepeating());

            final AudioTrackInfo info = trackScheduler.getAudioPlayer().getPlayingTrack().getInfo();
            String embedMessageDescription = "usunięta z";
            String embedMessageRemoveLoop = "";
            if (trackScheduler.isRepeating()) {
                embedMessageDescription = "umieszczona w";
                embedMessageRemoveLoop = String.format("Aby usunąć piosenkę z pętli, wpisz ponownie komendę `%s%s`.",
                        config.getDefPrefix(), MUSIC_LOOP.getCommandName());
            }

            final var embedMessage = new EmbedMessage(
                    String.format("PIOSENKA %s PĘTLI", embedMessageDescription.toUpperCase(Locale.ROOT)),
                    String.format(
                            "Piosenka **%s - %s** została %s nieskończonej pętli. %s",
                            info.author, info.title, embedMessageDescription, embedMessageRemoveLoop),
                    EmbedMessageColor.GREEN);
            event.getTextChannel().sendMessageEmbeds(embedMessage.buildMessage()).queue();

        } catch (EmptyAudioQueueException ex) {
            System.out.println(ex.getMessage());
        }
    }
}