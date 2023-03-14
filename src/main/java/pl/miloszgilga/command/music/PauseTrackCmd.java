/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PauseTrackCmd.java
 * Last modified: 11/03/2023, 10:06
 * Project name: jwizard-discord-bot
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

package pl.miloszgilga.command.music;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.MessageEmbed;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.dto.PauseTrackEmbedContent;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@JDAInjectableCommandLazyService
public class PauseTrackCmd extends AbstractMusicCommand {

    PauseTrackCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.PAUSE_TRACK, config, playerManager, embedBuilder);
        super.inPlayingMode = true;
        super.inListeningMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEvent event) {
        playerManager.pauseCurrentTrack(event);

        final AudioTrack track = playerManager.getMusicManager(event.getGuild()).getAudioPlayer().getPlayingTrack();
        final PauseTrackEmbedContent content = new PauseTrackEmbedContent(
            LocaleSet.PAUSE_TRACK_MESS,
            Map.of(
                "track", String.format("[%s](%s)", track.getInfo().title, track.getInfo().uri),
                "invoker", new EventWrapper(event).authorTag(),
                "resumeCmd", BotCommand.RESUME_TRACK.parseWithPrefix(config)
            ),
            Utilities.convertMilisToDate(track.getPosition()),
            Utilities.convertMilisToDate(track.getDuration() - track.getPosition()),
            Utilities.convertMilisToDate(track.getDuration())
        );
        final MessageEmbed messageEmbed = embedBuilder.createPauseTrackMessage(content);
        event.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
    }
}
