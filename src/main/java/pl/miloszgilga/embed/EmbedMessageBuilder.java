/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmbedMessageBuilder.java
 * Last modified: 05/03/2023, 23:38
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

package pl.miloszgilga.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import org.springframework.stereotype.Component;

import java.util.Map;

import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.dto.TrackEmbedContent;
import pl.miloszgilga.dto.PlaylistEmbedContent;
import pl.miloszgilga.dto.PauseTrackEmbedContent;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class EmbedMessageBuilder {

    private final BotConfiguration config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EmbedMessageBuilder(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(EventWrapper wrapper, String message, BugTracker bugTracker) {
        final String tracker = "`" + parseBugTracker(bugTracker) + "`";
        return new EmbedBuilder()
            .setAuthor(wrapper.authorTag(), null, wrapper.authorAvatarUrl())
            .setTitle(config.getLocaleText(LocaleSet.ERROR_HEADER))
            .setDescription(message)
            .appendDescription("\n\n" + config.getLocaleText(LocaleSet.BUG_TRACKER_MESS) + ": " + tracker)
            .setColor(EmbedColor.PURPLE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(EventWrapper wrapper, BotException ex) {
        return createErrorMessage(wrapper, ex.getMessage(), ex.getBugTracker());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createSingleTrackMessage(EventWrapper wrapper, TrackEmbedContent content) {
        return new EmbedBuilder()
            .setAuthor(wrapper.authorTag(), null, wrapper.authorAvatarUrl())
            .setDescription(config.getLocaleText(LocaleSet.ADD_NEW_TRACK_MESS))
            .addField(config.getLocaleText(LocaleSet.NEW_TRACK_NAME_MESS) + ":", content.trackUrl(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.TRACK_DURATION_TIME_MESS) + ":", content.durationTime(), true)
            .addField(config.getLocaleText(LocaleSet.TRACK_POSITION_IN_QUEUE_MESS) + ":", content.trackPosition(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.TRACK_ADDDED_BY_MESS) + ":", wrapper.authorTag(), true)
            .setThumbnail(content.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createPlaylistTracksMessage(EventWrapper wrapper, PlaylistEmbedContent content) {
        return new EmbedBuilder()
            .setAuthor(wrapper.authorTag(), null, wrapper.authorAvatarUrl())
            .setDescription(config.getLocaleText(LocaleSet.ADD_NEW_PLAYLIST_MESS))
            .addField(config.getLocaleText(LocaleSet.COUNT_OF_TRACKS_MESS) + ":", content.queueTracksCount(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.TRACKS_TOTAL_DURATION_TIME_MESS) + ":", content.queueDurationTime(), true)
            .addField(config.getLocaleText(LocaleSet.TRACK_ADDDED_BY_MESS) + ":", wrapper.authorTag(), true)
            .setThumbnail(content.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createPauseTrackMessage(PauseTrackEmbedContent content) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(content.localeSet(), content.localeVariables()))
            .addField(config.getLocaleText(LocaleSet.PAUSED_TRACK_TIME_MESS) + ":", content.pausedTimestamp(), true)
            .addField(config.getLocaleText(LocaleSet.PAUSED_TRACK_ESTIMATE_TIME_MESS) + ":", content.estimatedDuration(), true)
            .addField(config.getLocaleText(LocaleSet.PAUSED_TRACK_TOTAL_DURATION_MESS) + ":", content.totalDuration(), true)
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createMessage(LocaleSet localeSet, Map<String, Object> attributes) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(localeSet, attributes))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createMessage(LocaleSet localeSet) {
        return createMessage(localeSet, Map.of());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String parseBugTracker(BugTracker bugTracker) {
        final String projectVersion = config.getProjectVersion().replaceAll("\\.", "");
        return String.format("j%sb%s_exc%06d", Runtime.version().feature(), projectVersion, bugTracker.getId());
    }
}
