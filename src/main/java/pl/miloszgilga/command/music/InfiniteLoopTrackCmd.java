/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: InfiniteLoopTrackCmd.java
 * Last modified: 11/03/2023, 10:45
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

import net.dv8tion.jda.api.entities.MessageEmbed;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class InfiniteLoopTrackCmd extends AbstractMusicCommand {

    InfiniteLoopTrackCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.LOOP_TRACK, config, playerManager, embedBuilder);
        super.inPlayingMode = true;
        super.inListeningMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEvent event) {
        final boolean isRepeating = playerManager.toggleInfiniteLoopCurrentTrack(event);
        LocaleSet messsage = LocaleSet.REMOVE_TRACK_FROM_INFINITE_LOOP_MESS;
        if (isRepeating) {
            messsage = LocaleSet.ADD_TRACK_TO_INFINITE_LOOP_MESS;
        }
        final AudioTrackInfo playingTrack = playerManager.getCurrentPlayingTrack(event);
        final MessageEmbed messageEmbed = embedBuilder.createMessage(messsage, Map.of(
            "track", String.format("[%s](%s)", playingTrack.title, playingTrack.uri),
            "loopCmd", BotCommand.LOOP_TRACK.parseWithPrefix(config)
        ));
        event.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
    }
}
