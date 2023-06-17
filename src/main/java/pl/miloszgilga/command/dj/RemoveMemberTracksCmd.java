/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RemoveMemberTracksCmd.java
 * Last modified: 16/05/2023, 18:49
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
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class RemoveMemberTracksCmd extends AbstractDjCommand {

    private final EmbedPaginationBuilder pagination;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    RemoveMemberTracksCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        EmbedPaginationBuilder pagination, RemotePropertyHandler handler, CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(BotCommand.REMOVE_MEMBER_TRACKS, config, playerManager, embedBuilder, handler, cacheableCommandStateDao);
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
            ), event.getGuild());
        event.appendEmbedMessage(messageEmbed, () -> removedTracksList.display(event.getTextChannel()));
    }
}
