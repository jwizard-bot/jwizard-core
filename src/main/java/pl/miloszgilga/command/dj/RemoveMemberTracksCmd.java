/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RemoveMemberTracksCmd.java
 * Last modified: 18/03/2023, 21:43
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

package pl.miloszgilga.command.dj;

import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.dto.MemberRemovedTracksInfo;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.ExtendedAudioTrackInfo;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.embed.EmbedPaginationBuilder;
import pl.miloszgilga.command.AbstractDjCommand;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class RemoveMemberTracksCmd extends AbstractDjCommand {

    private final EmbedPaginationBuilder pagination;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    RemoveMemberTracksCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        EmbedPaginationBuilder pagination
    ) {
        super(BotCommand.REMOVE_MEMBER_TRACKS, config, playerManager, embedBuilder);
        super.onSameChannelWithBot = true;
        this.pagination = pagination;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteDjCommand(CommandEventWrapper event) {
        String userId = event.getArgumentAndParse(BotCommandArgument.REMOVE_TRACK_MEMBER_TAG);
        if (userId.contains("@")) userId = userId.replaceAll("[@<>]", "");

        final MemberRemovedTracksInfo removedTracksInfo = playerManager.removeTracksFromMember(event, userId);

        int i = 0;
        final List<String> pageableRemovedTracks = new ArrayList<>();
        for (final ExtendedAudioTrackInfo audioTrackInfo : removedTracksInfo.removedTracks()) {
            pageableRemovedTracks.add(Utilities.getRichPageableTrackInfo(++i, audioTrackInfo.getAudioTrack()));
        }

        final Paginator removedTracksList = pagination.createDefaultPaginator(pageableRemovedTracks);
        final MessageEmbed messageEmbed = embedBuilder
            .createMessage(ResLocaleSet.REMOVED_TRACKS_FROM_SELECTED_MEMBER_MESS, Map.of(
                "countOfRemovedTracks", pageableRemovedTracks.size(),
                "memberTag", removedTracksInfo.member().getUser().getAsTag()
            ));
        event.appendEmbedMessage(messageEmbed, () -> removedTracksList.display(event.getTextChannel()));
    }
}
