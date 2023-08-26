/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ShowQueueCmd.java
 * Last modified: 16/05/2023, 18:52
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

package pl.miloszgilga.command.music;

import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.entities.MessageEmbed;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;
import java.util.Queue;
import java.util.Objects;
import java.util.ArrayList;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.audioplayer.*;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.QueueEmbedContent;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.embed.EmbedPaginationBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import static pl.miloszgilga.exception.AudioPlayerException.TrackQueueIsEmptyException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class ShowQueueCmd extends AbstractMusicCommand {

    private final EmbedPaginationBuilder pagination;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ShowQueueCmd(
        BotConfiguration config, EmbedPaginationBuilder pagination, PlayerManager playerManager,
        EmbedMessageBuilder embedBuilder, RemotePropertyHandler handler, CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.QUEUE, config, playerManager, embedBuilder, handler, cacheableCommandStateDao);
        this.pagination = pagination;
        super.onSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final MusicManager musicManager = playerManager.getMusicManager(event);
        final Queue<AudioQueueExtendedInfo> tracks = musicManager.getQueue();
        if (tracks.isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        final List<String> pageableTracks = new ArrayList<>();
        int i = 0;
        for (final AudioQueueExtendedInfo aTrack : tracks) {
            final AudioTrack track = aTrack.audioTrack();
            pageableTracks.add(Utilities.getRichPageableTrackInfo(++i, track));
        }
        String leftToNextTrack = "-";
        final ExtendedAudioTrackInfo currentTrack = playerManager.getCurrentPlayingTrack(event);
        if (!Objects.isNull(currentTrack)) {
            leftToNextTrack = Utilities.convertMilisToDate(currentTrack.getApproxTime());
        }
        final long durationMilis = tracks.stream().mapToLong(t -> t.audioTrack().getDuration()).sum();

        final QueueEmbedContent content = new QueueEmbedContent(
            String.valueOf(tracks.size()),
            Utilities.convertMilisToDate(durationMilis),
            leftToNextTrack,
            Utilities.convertMilisToDate(musicManager.getActions().getAverageTrackDuration()),
            musicManager.isInfinitePlaylistActive() ? ResLocaleSet.TURN_ON_MESS : ResLocaleSet.TURN_OFF_MESS
        );
        final MessageEmbed messageEmbed = embedBuilder.createQueueInfoMessage(content, event.getGuild());
        final Paginator paginator = pagination.createDefaultPaginator(pageableTracks);

        event.appendEmbedMessage(messageEmbed, () -> paginator.display(event.getTextChannel()));
    }
}
