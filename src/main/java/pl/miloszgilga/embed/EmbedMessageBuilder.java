/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmbedMessageBuilder.java
 * Last modified: 17/05/2023, 01:42
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

package pl.miloszgilga.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;

import java.util.Map;

import pl.miloszgilga.dto.*;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.locale.ResLocaleSet;
import pl.miloszgilga.locale.VotingLocaleSet;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.vote.VoteFinishEmbedData;
import pl.miloszgilga.core.IEnumerableLocaleSet;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

import pl.miloszgilga.domain.guild_stats.GuildStatsEntity;
import pl.miloszgilga.domain.member_stats.MemberStatsEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class EmbedMessageBuilder {

    private final BotConfiguration config;
    private final RemotePropertyHandler handler;

    private final TriFunction<IEnumerableLocaleSet, String, Guild, MessageEmbed.Field> inlineField;
    private final TriFunction<IEnumerableLocaleSet, Number, Guild, MessageEmbed.Field> inlineNumField;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EmbedMessageBuilder(BotConfiguration config, RemotePropertyHandler handler) {
        this.config = config;
        this.handler = handler;
        this.inlineField  = (key, value, guild) -> new MessageEmbed.Field(config.getLocaleText(key, guild) + ":", value, true);
        this.inlineNumField = (key, value, guild) -> new MessageEmbed.Field(config.getLocaleText(key, guild) + ":",
            String.valueOf(value), true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(CommandEventWrapper wrapper, String message, BugTracker bugTracker) {
        final String tracker = "`" + parseBugTracker(bugTracker) + "`";
        final String messageLocale = config.getLocaleText(ResLocaleSet.BUG_TRACKER_MESS, wrapper.getGuild());
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(message)
            .appendDescription("\n\n" + messageLocale + ": " + tracker)
            .setColor(EmbedColor.PURPLE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(CommandEventWrapper wrapper, BotException ex) {
        return createErrorMessage(wrapper, ex.getMessage(), ex.getBugTracker());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createSingleTrackMessage(CommandEventWrapper wrapper, TrackEmbedContent c) {
        final String message = config.getLocaleText(ResLocaleSet.TRACK_ADDDED_BY_MESS, wrapper.getGuild());
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(ResLocaleSet.ADD_NEW_TRACK_MESS, wrapper.getGuild()))
            .addField(inlineField.apply(ResLocaleSet.TRACK_NAME_MESS, c.trackUrl(), wrapper.getGuild()))
            .addBlankField(true)
            .addField(inlineField.apply(ResLocaleSet.TRACK_DURATION_TIME_MESS, c.durationTime(), wrapper.getGuild()))
            .addField(inlineField.apply(ResLocaleSet.TRACK_POSITION_IN_QUEUE_MESS, c.trackPosition(), wrapper.getGuild()))
            .addBlankField(true)
            .addField(message + ":", wrapper.getAuthorTag(), true)
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createPlaylistTracksMessage(CommandEventWrapper wrapper, PlaylistEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(ResLocaleSet.ADD_NEW_PLAYLIST_MESS, wrapper.getGuild()))
            .addField(inlineField.apply(ResLocaleSet.COUNT_OF_TRACKS_MESS, c.queueTracksCount(), wrapper.getGuild()))
            .addBlankField(true)
            .addField(inlineField.apply(ResLocaleSet.TRACKS_TOTAL_DURATION_TIME_MESS, c.queueDurationTime(), wrapper.getGuild()))
            .addField(inlineField.apply(ResLocaleSet.TRACK_ADDDED_BY_MESS, wrapper.getAuthorTag(), wrapper.getGuild()))
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createPauseTrackMessage(PauseTrackEmbedContent c, Guild guild) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(c.localeSet(), guild, c.localeVariables()))
            .addField(StringUtils.EMPTY, c.pausedVisualizationTrack(), false)
            .addField(inlineField.apply(ResLocaleSet.PAUSED_TRACK_TIME_MESS, c.pausedTimestamp(), guild))
            .addField(inlineField.apply(ResLocaleSet.PAUSED_TRACK_ESTIMATE_TIME_MESS, c.estimatedDuration(), guild))
            .addField(inlineField.apply(ResLocaleSet.PAUSED_TRACK_TOTAL_DURATION_MESS, c.totalDuration(), guild))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createCurrentPlayingMessage(CommandEventWrapper wrapper, CurrentPlayEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(c.playingPauseMessage(), wrapper.getGuild()))
            .addField(inlineField.apply(ResLocaleSet.TRACK_NAME_MESS, c.trackUrl(), wrapper.getGuild()))
            .addBlankField(true)
            .addField(inlineField.apply(ResLocaleSet.TRACK_ADDDED_BY_MESS, c.addedByTag(), wrapper.getGuild()))
            .addField(StringUtils.EMPTY, c.playerPercentageTrack(), false)
            .addField(config.getLocaleText(c.playingVisualizationTrack(), wrapper.getGuild()), c.timestampNowAndMax(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(ResLocaleSet.CURRENT_TRACK_LEFT_TO_NEXT_MESS, wrapper.getGuild()), c.leftToNextTrack(), true)
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createHelpMessage(CommandEventWrapper wrapper, HelpEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(c.description())
            .addField(inlineField.apply(ResLocaleSet.HELP_INFO_COMPILATION_VERSION_MESS, c.compilationVersion(), wrapper.getGuild()))
            .addField(inlineField.apply(ResLocaleSet.HELP_INFO_COUNT_OF_AVAILABLE_COMMANDS_MESS, c.availableCommandsCount(), wrapper.getGuild()))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createQueueInfoMessage(QueueEmbedContent c, Guild guild) {
        return new EmbedBuilder()
            .addField(inlineField.apply(ResLocaleSet.ALL_TRACKS_IN_QUEUE_COUNT_MESS, c.queueSize(), guild))
            .addBlankField(true)
            .addField(inlineField.apply(ResLocaleSet.ALL_TRACKS_IN_QUEUE_DURATION_MESS, c.queueMaxDuration(), guild))
            .addField(inlineField.apply(ResLocaleSet.APPROX_TO_NEXT_TRACK_FROM_QUEUE_MESS, c.approxToNextTrack(), guild))
            .addBlankField(true)
            .addField(inlineField.apply(ResLocaleSet.PLAYLIST_AVERAGE_TRACK_DURATION_MESS, c.averageSingleTrackDuration(), guild))
            .addField(inlineField.apply(ResLocaleSet.PLAYLIST_REPEATING_MODE_MESS, config.getLocaleText(c.repeatingMode(), guild), guild))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createTrackMessage(ResLocaleSet localeSet, Map<String, Object> attributes, String thumbnail, Guild guild) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(localeSet, guild, attributes))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .setThumbnail(thumbnail)
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createInitialVoteMessage(CommandEventWrapper wrapper, ResLocaleSet locale, Map<String, Object> attrs) {
        final long maxVotingTime = handler.getPossibleRemoteProperty(RemoteProperty.R_INACTIVITY_VOTING_TIMEOUT,
            wrapper.getGuild(), Long.class);
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(locale, wrapper.getGuild(), attrs))
            .setFooter(config.getLocaleText(VotingLocaleSet.MAX_TIME_VOTING, wrapper.getGuild()) + ": " +
                Utilities.convertSecondsToMinutes(maxVotingTime))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createResponseVoteMessage(
        VotingLocaleSet title, String desc, VoteFinishEmbedData res, EmbedColor color, Guild guild
    ) {
        final String yesNo = String.format("%d/%d", res.votesForYes(), res.votesForNo());
        final String requredTotal = String.format("%d/%d", res.requredVotes(), res.totalVotes());
        final byte votingRatio = handler.getPossibleRemoteProperty(RemoteProperty.R_VOTING_PERCENTAGE_RATIO, guild, Byte.class);
        return new EmbedBuilder()
            .setTitle(config.getLocaleText(title, guild))
            .setDescription(desc)
            .addField(inlineField.apply(VotingLocaleSet.VOTES_FOR_YES_NO_VOTING, yesNo, guild))
            .addField(inlineField.apply(VotingLocaleSet.REQUIRED_TOTAL_VOTES_VOTING, requredTotal, guild))
            .addField(inlineField.apply(VotingLocaleSet.VOTES_RATIO_VOTING, votingRatio + "%", guild))
            .setColor(color.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createMemberStatsMessage(CommandEventWrapper wrapper, MemberStatsEntity stats) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .addField(inlineNumField.apply(ResLocaleSet.MESSAGES_SENDED_MESS, stats.getMessagesSended(), wrapper.getGuild()))
            .addBlankField(true)
            .addField(inlineNumField.apply(ResLocaleSet.MESSAGES_UPDATED_MESS, stats.getMessagesUpdated(), wrapper.getGuild()))
            .addField(inlineNumField.apply(ResLocaleSet.REACTIONS_ADDED_MESS, stats.getReactionsAdded(), wrapper.getGuild()))
            .addBlankField(true)
            .addField(inlineNumField.apply(ResLocaleSet.LEVEL_MESS, stats.getLevel(), wrapper.getGuild()))
            .addField(inlineNumField.apply(ResLocaleSet.SLASH_INTERACTIONS_MESS, stats.getSlashInteractions(), wrapper.getGuild()))
            .setFooter(config.getLocaleText(ResLocaleSet.GENERATED_DATE_MESS, wrapper.getGuild()) + ": " + Utilities.getFormattedUTCNow())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createGuildStatsMessage(CommandEventWrapper wrapper, GuildStatsEntity stats, GuildMembersStatsDto dto) {
        final Guild guild = wrapper.getGuild();
        final long serverBotsCount = guild.getMembers().stream().filter(u -> u.getUser().isBot()).count();
        return new EmbedBuilder()
            .setAuthor(wrapper.getGuildName(), null, wrapper.getGuild().getIconUrl())
            .addField(inlineNumField.apply(ResLocaleSet.GUILD_USERS_COUNT_MESS, guild.getMemberCount(), guild))
            .addBlankField(true)
            .addField(inlineNumField.apply(ResLocaleSet.GUILD_BOTS_COUNT_MESS, serverBotsCount, guild))
            .addField(inlineNumField.apply(ResLocaleSet.GUILD_BOOSTERS_COUNT_MESS, guild.getBoosters().size(), guild))
            .addBlankField(true)
            .addField(inlineNumField.apply(ResLocaleSet.GUILD_BOOSTING_LEVEL_MESS, guild.getBoostCount(), guild))
            .addField(inlineNumField.apply(ResLocaleSet.MESSAGES_SENDED_MESS, dto.messagesSended(), guild))
            .addBlankField(true)
            .addField(inlineNumField.apply(ResLocaleSet.MESSAGES_UPDATED_MESS, dto.messagesUpdated(), guild))
            .addField(inlineNumField.apply(ResLocaleSet.REACTIONS_ADDED_MESS, dto.reactionsAdded(), guild))
            .addBlankField(true)
            .addField(inlineNumField.apply(ResLocaleSet.MESSAGES_DELETED_MESS, stats.getMessagesDeleted(), guild))
            .addField(inlineNumField.apply(ResLocaleSet.REACTIONS_DELETED_MESS, stats.getReactionsDeleted(), guild))
            .addBlankField(true)
            .addField(inlineNumField.apply(ResLocaleSet.SLASH_INTERACTIONS_MESS, dto.slashInteractions(), guild))
            .setFooter(config.getLocaleText(ResLocaleSet.GENERATED_DATE_MESS, guild) + ": " + Utilities.getFormattedUTCNow())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createMessage(IEnumerableLocaleSet localeSet, Map<String, Object> attributes, Guild guild) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(localeSet, guild, attributes))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    public MessageEmbed createMessage(IEnumerableLocaleSet localeSet, Guild guild) {
        return createMessage(localeSet, Map.of(), guild);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createInitialVoteMessage(CommandEventWrapper wrapper, ResLocaleSet locale) {
        return createInitialVoteMessage(wrapper, locale, Map.of());
    }

    public MessageEmbed createSuccessVoteMessage(
        ResLocaleSet locale, Map<String, Object> attrs, VoteFinishEmbedData res, Guild guild
    ) {
        final String mess = config.getLocaleText(locale, guild, attrs);
        return createResponseVoteMessage(VotingLocaleSet.ON_SUCCESS_VOTING, mess, res, EmbedColor.ANTIQUE_WHITE, guild);
    }

    public MessageEmbed createSuccessVoteMessage(ResLocaleSet locale, VoteFinishEmbedData res, Guild guild) {
        final String mess = config.getLocaleText(locale, guild);
        return createResponseVoteMessage(VotingLocaleSet.ON_SUCCESS_VOTING, mess, res, EmbedColor.ANTIQUE_WHITE, guild);
    }

    public MessageEmbed createFailureVoteMessage(
        ResLocaleSet locale, Map<String, Object> attrs, VoteFinishEmbedData res, Guild guild
    ) {
        final String mess = config.getLocaleText(VotingLocaleSet.TOO_FEW_POSITIVE_VOTES_VOTING, guild) + ". "
            + config.getLocaleText(locale, guild, attrs);
        return createResponseVoteMessage(VotingLocaleSet.ON_FAILURE_VOTING, mess, res, EmbedColor.PURPLE, guild);
    }

    public MessageEmbed createFailureVoteMessage(ResLocaleSet locale, VoteFinishEmbedData res, Guild guild) {
        final String mess = config.getLocaleText(VotingLocaleSet.TOO_FEW_POSITIVE_VOTES_VOTING, guild) + ". "
            + config.getLocaleText(locale, guild);
        return createResponseVoteMessage(VotingLocaleSet.ON_FAILURE_VOTING, mess, res, EmbedColor.PURPLE, guild);
    }

    public MessageEmbed createTimeoutVoteMessage(
        ResLocaleSet locale, Map<String, Object> attrs, VoteFinishEmbedData res, Guild guild
    ) {
        final String mess = config.getLocaleText(locale, guild, attrs);
        return createResponseVoteMessage(VotingLocaleSet.ON_TIMEOUT_VOTING, mess, res, EmbedColor.PURPLE, guild);
    }

    public MessageEmbed createTimeoutVoteMessage(ResLocaleSet locale, VoteFinishEmbedData res, Guild guild) {
        final String mess = config.getLocaleText(locale, guild);
        return createResponseVoteMessage(VotingLocaleSet.ON_TIMEOUT_VOTING, mess, res, EmbedColor.PURPLE, guild);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String parseBugTracker(BugTracker bugTracker) {
        final String projectVersion = config.getProjectVersion().replaceAll("\\.", "");
        return String.format("j%sb%s_exc%06d", Runtime.version().feature(), projectVersion, bugTracker.getId());
    }
}
