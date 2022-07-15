/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AudioPlayCommandExecutor.java
 * Last modified: 11/07/2022, 23:35
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
import net.dv8tion.jda.api.entities.Member;
import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.net.URISyntaxException;

import static pl.miloszgilga.FranekBot.config;
import static pl.miloszgilga.Command.MUSIC_PLAY;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.exceptions.MusicBotIsUseException;
import pl.miloszgilga.exceptions.IllegalCommandArgumentsException;
import pl.miloszgilga.exceptions.UserOnVoiceChannelNotFoundException;


public class AudioPlayCommandExecutor extends Command {

    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    public AudioPlayCommandExecutor() {
        name = MUSIC_PLAY.getCommandName();
        help = MUSIC_PLAY.getCommandDescription();
    }

    @Override
    @Description("command: <[prefix]play [music link or description]>")
    protected void execute(CommandEvent event) {
        try {
            if (event.getArgs().split(" ").length < 1) {
                throw new IllegalCommandArgumentsException(event, String.format(
                        "`%s [link lub nazwa piosenki]`", config.getDefPrefix() + MUSIC_PLAY.getCommandName()));
            }

            checkIfBotIsCurrentyUsedOnAnotherChannel(event);
            if (!Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).inAudioChannel()) {
                throw new UserOnVoiceChannelNotFoundException(event, "Aby możliwe było odtworzenie piosenki, " +
                        "musisz znajdować się na kanale głosowym.");
            }

            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            audioManager.openAudioConnection(memberChannel);

            String withoutPrefix = event.getArgs();
            if (!isUrl(withoutPrefix) && event.getArgs().split(" ").length > 1) {
                withoutPrefix = "ytsearch: " + withoutPrefix + " audio";
            } else {
                withoutPrefix = withoutPrefix.replaceAll(" ", "");
            }
            playerManager.loadAndPlay(event.getTextChannel(), withoutPrefix);

        } catch (UserOnVoiceChannelNotFoundException | MusicBotIsUseException | IllegalCommandArgumentsException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void checkIfBotIsCurrentyUsedOnAnotherChannel(CommandEvent event) {
        final String guildId = event.getGuild().getId();
        Optional<VoiceChannel> findBotOnVoiceChannel = Objects.requireNonNull(event.getJDA().getGuildById(guildId))
                .getVoiceChannels().stream().filter(channel -> {
                    Member botMember = event.getGuild().getMember(event.getJDA().getSelfUser());
                    Member senderUserMember = event.getGuild().getMember(event.getAuthor());
                    return channel.getMembers().contains(botMember) && !channel.getMembers().contains(senderUserMember);
                })
                .findFirst();
        if (findBotOnVoiceChannel.isPresent()) {
            throw new MusicBotIsUseException(event);
        }
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}