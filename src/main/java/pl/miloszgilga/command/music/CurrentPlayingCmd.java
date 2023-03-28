/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CurrentPlayingCmd.java
 * Last modified: 19/03/2023, 14:56
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.command.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.CurrentPlayEmbedContent;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.ExtendedAudioTrackInfo;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class CurrentPlayingCmd extends AbstractMusicCommand {

    CurrentPlayingCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.CURRENT_PLAYING, config, playerManager, embedBuilder);
        super.inPlayingMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final MusicManager musicManager = playerManager.getMusicManager(event);
        final ExtendedAudioTrackInfo track = playerManager.getCurrentPlayingTrack(event);

        final String trackTimestamp = Utilities.convertMilisToDate(track.getTimestamp());
        final String trackMaxDuration = Utilities.convertMilisToDate(track.getMaxDuration());

        final CurrentPlayEmbedContent content = new CurrentPlayEmbedContent(
            LocaleSet.CURRENT_PLAYING_TRACK_MESS,
            LocaleSet.CURRENT_PLAYING_TIMESTAMP_MESS,
            Utilities.getRichTrackTitle(track),
            track.getThumbnailUrl(),
            ((Member) musicManager.getAudioPlayer().getPlayingTrack().getUserData()).getUser().getAsTag(),
            String.format("%s / %s", trackTimestamp, trackMaxDuration),
            Utilities.convertMilisToDate(track.getMaxDuration() - track.getTimestamp()),
            Utilities.createPlayerPercentageTrack(track.getTimestamp(), track.getMaxDuration())
        );
        final MessageEmbed messageEmbed = embedBuilder.createCurrentPlayingMessage(event, content);
        event.appendEmbedMessage(messageEmbed);
    }
}
