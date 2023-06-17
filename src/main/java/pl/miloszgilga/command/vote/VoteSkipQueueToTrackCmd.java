/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: VoteSkipQueueToTrackCmd.java
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
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
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
        RemotePropertyHandler handler, CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.VOTE_SKIP_TO_TRACK, config, playerManager, embedBuilder, handler, cacheableCommandStateDao);
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
