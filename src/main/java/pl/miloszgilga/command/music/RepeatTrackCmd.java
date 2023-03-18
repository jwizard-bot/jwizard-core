/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RepeatTrackCmd.java
 * Last modified: 11/03/2023, 10:39
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
import org.apache.commons.lang3.math.NumberUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import static pl.miloszgilga.exception.AudioPlayerException.TrackRepeatsOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class RepeatTrackCmd extends AbstractMusicCommand {

    RepeatTrackCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.REPEAT_TRACK, config, playerManager, embedBuilder);
        super.inPlayingMode = true;
        super.inListeningMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final int repeats = NumberUtils.toInt(event.args());
        if (repeats < 1 || repeats > config.getProperty(BotProperty.J_MAX_REPEATS_SINGLE_TRACK, Integer.class)) {
            throw new TrackRepeatsOutOfBoundsException(config, event);
        }
        playerManager.repeatCurrentTrack(event, repeats);

        final AudioTrackInfo trackInfo = playerManager.getCurrentPlayingTrack(event);
        final MessageEmbed messageEmbed = embedBuilder
            .createMessage(LocaleSet.SET_MULTIPLE_REPEATING_TRACK_MESS, Map.of(
                "track", String.format("[%s](%s)", trackInfo.title, trackInfo.uri),
                "times", repeats,
                "clearRepeatingCmd", BotCommand.CLEAR_REPEAT_TRACK.parseWithPrefix(config)
            ));
        event.textChannel().sendMessageEmbeds(messageEmbed).queue();
    }
}
