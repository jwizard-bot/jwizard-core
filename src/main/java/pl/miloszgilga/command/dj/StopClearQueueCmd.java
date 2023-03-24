/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: StopAndClearQueueCmd.java
 * Last modified: 18/03/2023, 09:27
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

package pl.miloszgilga.command.dj;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;
import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.TrackScheduler;
import pl.miloszgilga.audioplayer.ExtendedAudioTrackInfo;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractDjCommand;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class StopClearQueueCmd extends AbstractDjCommand {

    StopClearQueueCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.STOP_CLEAR_QUEUE, config, playerManager, embedBuilder);
        super.inSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteDjCommand(CommandEventWrapper event) {
        final MusicManager musicManager = playerManager.getMusicManager(event);
        final ExtendedAudioTrackInfo currentTrack = playerManager.getCurrentPlayingTrack(event);
        musicManager.getTrackScheduler().clearAndDestroy(false);
        MessageEmbed messageEmbed;
        if (!Objects.isNull(currentTrack)) {
            messageEmbed = embedBuilder.createMessage(LocaleSet.SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE_MESS, Map.of(
                "currentTrack", TrackScheduler.getRichTrackTitle(currentTrack)
            ));
        } else {
            messageEmbed = embedBuilder.createMessage(LocaleSet.CLEAR_QUEUE_MESS);
        }
        event.appendEmbedMessage(messageEmbed);
    }
}