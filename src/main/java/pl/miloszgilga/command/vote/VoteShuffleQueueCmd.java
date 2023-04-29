/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: VoteShuffleQueueCmd.java
 * Last modified: 18/03/2023, 23:13
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

import java.util.Map;

import pl.miloszgilga.BotCommand;
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class VoteShuffleQueueCmd extends AbstractVoteMusicCommand {

    VoteShuffleQueueCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.VOTE_SHUFFLE_QUEUE, config, playerManager, embedBuilder, handler);
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected VoteEmbedResponse doExecuteVoteMusicCommand(CommandEventWrapper event) {
        final MusicManager musicManager = playerManager.getMusicManager(event);
        if (musicManager.getQueue().isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        final Map<String, Object> attributes = Map.of(
            "queueTracksCount", musicManager.getQueue().size()
        );
        return new VoteEmbedResponse(
            VoteShuffleQueueCmd.class,
            embedBuilder.createInitialVoteMessage(event, ResLocaleSet.VOTE_SUFFLE_QUEUE_MESS, attributes),
            ed -> {
                playerManager.shuffleQueue(event);
                return embedBuilder.createSuccessVoteMessage(ResLocaleSet.SUCCESS_VOTE_SUFFLE_QUEUE_MESS, attributes, ed);
            },
            ed -> embedBuilder.createFailureVoteMessage(ResLocaleSet.FAILURE_VOTE_SUFFLE_QUEUE_MESS, attributes, ed),
            ed -> embedBuilder.createTimeoutVoteMessage(ResLocaleSet.FAILURE_VOTE_SUFFLE_QUEUE_MESS, attributes, ed)
        );
    }
}
