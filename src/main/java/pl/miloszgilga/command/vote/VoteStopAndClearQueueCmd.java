/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: VoteStopAndClearQueueCmd.java
 * Last modified: 17/05/2023, 01:30
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.miloszgilga.command.vote;

import net.dv8tion.jda.api.entities.Guild;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.vote.VoteEmbedResponse;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.SchedulerActions;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractVoteMusicCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class VoteStopAndClearQueueCmd extends AbstractVoteMusicCommand {

    VoteStopAndClearQueueCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.VOTE_STOP_CLEAR_QUEUE, config, playerManager, embedBuilder, handler);
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected VoteEmbedResponse doExecuteVoteMusicCommand(CommandEventWrapper event) {
        final SchedulerActions actions = playerManager.getMusicManager(event).getActions();
        final Guild g = event.getGuild();
        return new VoteEmbedResponse(
            VoteStopAndClearQueueCmd.class,
            embedBuilder.createInitialVoteMessage(event, ResLocaleSet.VOTE_STOP_CLEAR_QUEUE_MESS),
            ed -> {
                actions.clearAndDestroy(false);
                actions.leaveAndSendMessageAfterInactivity();
                return embedBuilder.createSuccessVoteMessage(ResLocaleSet.SUCCESS_VOTE_STOP_CLEAR_QUEUE_MESS, ed, g);
            },
            ed -> embedBuilder.createFailureVoteMessage(ResLocaleSet.FAILURE_VOTE_STOP_CLEAR_QUEUE_MESS, ed, g),
            ed -> embedBuilder.createTimeoutVoteMessage(ResLocaleSet.FAILURE_VOTE_STOP_CLEAR_QUEUE_MESS, ed, g)
        );
    }
}
