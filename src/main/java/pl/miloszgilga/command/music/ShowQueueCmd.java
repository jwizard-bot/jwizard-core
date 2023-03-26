/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ShowQueueCmd.java
 * Last modified: 19/03/2023, 22:55
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
 * The software is provided “as is”, without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
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
import pl.miloszgilga.dto.QueueEmbedContent;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.embed.EmbedPaginationBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
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
        EmbedMessageBuilder embedBuilder
    ) {
        super(BotCommand.QUEUE, config, playerManager, embedBuilder);
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
            Utilities.convertMilisToDate(musicManager.getTrackScheduler().getAverageTrackDuration())
        );
        final MessageEmbed messageEmbed = embedBuilder.createQueueInfoMessage(content);
        final Paginator paginator = pagination.createDefaultPaginator(pageableTracks);

        event.appendEmbedMessage(messageEmbed, () -> paginator.display(event.getTextChannel()));
    }
}
