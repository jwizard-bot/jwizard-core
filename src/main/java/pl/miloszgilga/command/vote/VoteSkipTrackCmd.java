/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: VoteSkipTrackCmd.java
 * Last modified: 18/03/2023, 21:42
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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.vote.VoteEmbedResponse;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractVoteMusicCommand;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class VoteSkipTrackCmd extends AbstractVoteMusicCommand {

    VoteSkipTrackCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.VOTE_SKIP_TRACK, config, playerManager, embedBuilder);
        super.onSameChannelWithBot = true;
        super.inPlayingMode = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected VoteEmbedResponse doExecuteVoteMusicCommand(CommandEventWrapper event) {
        final AudioTrack skippedCurrentPlaying = playerManager.getCurrentPlayingTrack(event).getAudioTrack();

        final Map<String, Object> attributes = Map.of(
            "audioTrack", Utilities.getRichTrackTitle(skippedCurrentPlaying.getInfo())
        );
        return new VoteEmbedResponse(
            VoteSkipTrackCmd.class,
            embedBuilder.createInitialVoteMessage(event, ResLocaleSet.VOTE_SKIP_TRACK_MESS, attributes),
            ed -> {
                playerManager.skipCurrentTrack(event);
                return embedBuilder.createSuccessVoteMessage(ResLocaleSet.SUCCESS_VOTE_SKIP_TRACK_MESS, attributes, ed);
            },
            ed -> embedBuilder.createFailureVoteMessage(ResLocaleSet.FAILURE_VOTE_SKIP_TRACK_MESS, attributes, ed),
            ed -> embedBuilder.createTimeoutVoteMessage(ResLocaleSet.FAILURE_VOTE_SKIP_TRACK_MESS, attributes, ed)
        );
    }
}
