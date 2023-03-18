/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ShowQueueCmd.java
 * Last modified: 18/03/2023, 11:18
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

import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.entities.MessageEmbed;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;
import java.util.Queue;
import java.util.Objects;
import java.util.ArrayList;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.dto.QueueEmbedContent;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.TrackScheduler;
import pl.miloszgilga.audioplayer.AudioQueueExtendedInfo;
import pl.miloszgilga.audioplayer.ExtendedAudioTrackInfo;
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
        super.inSameChannelWithBot = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final Queue<AudioQueueExtendedInfo> tracks = playerManager.getMusicManager(event).getQueue();
        if (tracks.isEmpty()) {
            throw new TrackQueueIsEmptyException(config, event);
        }
        final List<String> pageableTracks = new ArrayList<>();
        int i = 0;
        for (final AudioQueueExtendedInfo aTrack : tracks) {
            final AudioTrack track = aTrack.audioTrack();
            pageableTracks.add(String.format("`%d`. [ %s ]\n**%s**",
                ++i,
                Utilities.convertMilisToDate(track.getDuration()),
                TrackScheduler.getRichTrackTitle(track.getInfo())
            ));
        }
        String leftToNextTrack = "-";
        final ExtendedAudioTrackInfo currentTrack = playerManager.getCurrentPlayingTrack(event);
        if (!Objects.isNull(currentTrack)) {
            leftToNextTrack = Utilities.convertMilisToDate(currentTrack.getApproxTime());
        }
        final long durationMilis = tracks.stream().mapToLong(t -> t.audioTrack().getDuration()).sum();

        final QueueEmbedContent content = new QueueEmbedContent(String.valueOf(tracks.size()),
            Utilities.convertMilisToDate(durationMilis), leftToNextTrack);
        final MessageEmbed messageEmbed = embedBuilder.createQueueInfoMessage(content);
        final Paginator paginator = pagination.createDefaultPaginator(pageableTracks);

        event.textChannel().sendMessageEmbeds(messageEmbed).queue();
        paginator.display(event.textChannel());
    }
}
