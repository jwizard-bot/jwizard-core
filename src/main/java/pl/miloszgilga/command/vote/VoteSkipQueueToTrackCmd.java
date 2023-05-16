/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: VoteSkipQueueToTrackCmd.java
 * Last modified: 04/04/2023, 14:20
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

package pl.miloszgilga.command.vote;

import net.dv8tion.jda.api.entities.Guild;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.vote.VoteEmbedResponse;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractVoteMusicCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import static pl.miloszgilga.exception.AudioPlayerException.TrackQueueIsEmptyException;
import static pl.miloszgilga.exception.AudioPlayerException.TrackPositionOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class VoteSkipQueueToTrackCmd extends AbstractVoteMusicCommand {

    VoteSkipQueueToTrackCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.VOTE_SKIP_TO_TRACK, config, playerManager, embedBuilder, handler);
        super.onSameChannelWithBot = true;
        super.inPlayingMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected VoteEmbedResponse doExecuteVoteMusicCommand(CommandEventWrapper event) {
        final MusicManager musicManager = playerManager.getMusicManager(event);
        final Integer trackPos = event.getArgumentAndParse(BotCommandArgument.VOTE_SKIP_TRACK_POSITION);

        if (musicManager.getQueue().isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        if (musicManager.getActions().checkInvTrackPosition(trackPos)) {
            throw new TrackPositionOutOfBoundsException(config, event, musicManager.getQueue().size());
        }
        final AudioTrack currentPlaying = musicManager.getAudioPlayer().getPlayingTrack();
        final AudioTrack trackToSkipped = musicManager.getActions().getTrackByPosition(trackPos);

        final Map<String, Object> attributes = Map.of(
            "audioTrack", Utilities.getRichTrackTitle(currentPlaying.getInfo()),
            "nextAudioTrack", Utilities.getRichTrackTitle(trackToSkipped.getInfo()),
            "countOfSkipped", String.valueOf(trackPos - 1)
        );
        final Guild g = event.getGuild();
        return new VoteEmbedResponse(
            VoteSkipQueueToTrackCmd.class,
            embedBuilder.createInitialVoteMessage(event, ResLocaleSet.VOTE_SKIP_TO_TRACK_MESS, attributes),
            ed -> {
                playerManager.skipToTrackPos(event, trackPos);
                return embedBuilder.createSuccessVoteMessage(ResLocaleSet.SUCCESS_VOTE_SKIP_TRACK_MESS, attributes, ed, g);
            },
            ed -> embedBuilder.createFailureVoteMessage(ResLocaleSet.FAILURE_VOTE_SKIP_TO_TRACK_MESS, attributes, ed, g),
            ed -> embedBuilder.createTimeoutVoteMessage(ResLocaleSet.FAILURE_VOTE_SKIP_TO_TRACK_MESS, attributes, ed, g)
        );
    }
}
