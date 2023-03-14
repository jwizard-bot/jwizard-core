/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ClearRepeatTrackCmd.java
 * Last modified: 12/03/2023, 16:26
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

@Slf4j
@JDAInjectableCommandLazyService
public class ClearRepeatTrackCmd extends AbstractMusicCommand {

    ClearRepeatTrackCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.CLEAR_REPEAT_TRACK, config, playerManager, embedBuilder);
        super.inPlayingMode = false;
        super.inListeningMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEvent event) {
        playerManager.repeatCurrentTrack(event, 0);

        final AudioTrackInfo trackInfo = playerManager.getCurrentPlayingTrack(event);
        final MessageEmbed messageEmbed = embedBuilder
            .createMessage(LocaleSet.REMOVE_MULTIPLE_REPEATING_TRACK_MESS, Map.of(
                "track", String.format("[%s](%s)", trackInfo.title, trackInfo.uri),
                "repeatingCmd", BotCommand.REPEAT_TRACK.parseWithPrefix(config)
            ));
        event.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
    }
}
