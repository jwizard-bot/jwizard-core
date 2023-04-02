/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmbedMessageBuilder.java
 * Last modified: 19/03/2023, 22:52
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

package pl.miloszgilga.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiFunction;

import pl.miloszgilga.dto.*;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class EmbedMessageBuilder {

    private final BotConfiguration config;
    private final BiFunction<LocaleSet, String, MessageEmbed.Field> inlineField;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EmbedMessageBuilder(BotConfiguration config) {
        this.config = config;
        this.inlineField  = (key, value) -> new MessageEmbed.Field(config.getLocaleText(key) + ":", value, true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(CommandEventWrapper wrapper, String message, BugTracker bugTracker) {
        final String tracker = "`" + parseBugTracker(bugTracker) + "`";
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setTitle(config.getLocaleText(LocaleSet.ERROR_HEADER))
            .setDescription(message)
            .appendDescription("\n\n" + config.getLocaleText(LocaleSet.BUG_TRACKER_MESS) + ": " + tracker)
            .setColor(EmbedColor.PURPLE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(CommandEventWrapper wrapper, BotException ex) {
        return createErrorMessage(wrapper, ex.getMessage(), ex.getBugTracker());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createSingleTrackMessage(CommandEventWrapper wrapper, TrackEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(LocaleSet.ADD_NEW_TRACK_MESS))
            .addField(inlineField.apply(LocaleSet.TRACK_NAME_MESS, c.trackUrl()))
            .addBlankField(true)
            .addField(inlineField.apply(LocaleSet.TRACK_DURATION_TIME_MESS, c.durationTime()))
            .addField(inlineField.apply(LocaleSet.TRACK_POSITION_IN_QUEUE_MESS, c.trackPosition()))
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.TRACK_ADDDED_BY_MESS) + ":", wrapper.getAuthorTag(), true)
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createPlaylistTracksMessage(CommandEventWrapper wrapper, PlaylistEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(LocaleSet.ADD_NEW_PLAYLIST_MESS))
            .addField(inlineField.apply(LocaleSet.COUNT_OF_TRACKS_MESS, c.queueTracksCount()))
            .addBlankField(true)
            .addField(inlineField.apply(LocaleSet.TRACKS_TOTAL_DURATION_TIME_MESS, c.queueDurationTime()))
            .addField(inlineField.apply(LocaleSet.TRACK_ADDDED_BY_MESS, wrapper.getAuthorTag()))
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createPauseTrackMessage(PauseTrackEmbedContent c) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(c.localeSet(), c.localeVariables()))
            .addField(StringUtils.EMPTY, c.pausedVisualizationTrack(), false)
            .addField(inlineField.apply(LocaleSet.PAUSED_TRACK_TIME_MESS, c.pausedTimestamp()))
            .addField(inlineField.apply(LocaleSet.PAUSED_TRACK_ESTIMATE_TIME_MESS, c.estimatedDuration()))
            .addField(inlineField.apply(LocaleSet.PAUSED_TRACK_TOTAL_DURATION_MESS, c.totalDuration()))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createCurrentPlayingMessage(CommandEventWrapper wrapper, CurrentPlayEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(c.playingPauseMessage()))
            .addField(inlineField.apply(LocaleSet.TRACK_NAME_MESS, c.trackUrl()))
            .addBlankField(true)
            .addField(inlineField.apply(LocaleSet.TRACK_ADDDED_BY_MESS, c.addedByTag()))
            .addField(StringUtils.EMPTY, c.playerPercentageTrack(), false)
            .addField(config.getLocaleText(c.playingVisualizationTrack()), c.timestampNowAndMax(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.CURRENT_TRACK_LEFT_TO_NEXT_MESS), c.leftToNextTrack(), true)
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createHelpMessage(CommandEventWrapper wrapper, HelpEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(c.description())
            .addField(inlineField.apply(LocaleSet.HELP_INFO_COMPILATION_VERSION_MESS, c.compilationVersion()))
            .addField(inlineField.apply(LocaleSet.HELP_INFO_COUNT_OF_AVAILABLE_COMMANDS_MESS, c.availableCommandsCount()))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createQueueInfoMessage(QueueEmbedContent c) {
        return new EmbedBuilder()
            .addField(inlineField.apply(LocaleSet.ALL_TRACKS_IN_QUEUE_COUNT_MESS, c.queueSize()))
            .addBlankField(true)
            .addField(inlineField.apply(LocaleSet.ALL_TRACKS_IN_QUEUE_DURATION_MESS, c.queueMaxDuration()))
            .addField(inlineField.apply(LocaleSet.APPROX_TO_NEXT_TRACK_FROM_QUEUE_MESS, c.approxToNextTrack()))
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.PLAYLIST_AVERAGE_TRACK_DURATION) + ":", c.averageSingleTrackDuration(), true)
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createTrackMessage(LocaleSet localeSet, Map<String, Object> attributes, String thumbnail) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(localeSet, attributes))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .setThumbnail(thumbnail)
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
