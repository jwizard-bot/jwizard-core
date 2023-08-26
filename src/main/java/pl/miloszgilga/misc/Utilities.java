/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: Utilities.java
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

package pl.miloszgilga.misc;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import org.apache.commons.lang3.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.time.ZoneOffset;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.time.format.DateTimeFormatter;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.core.remote.RemoteProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.CommandException.UserNotFoundInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public final class Utilities {

    private static final DateTimeFormatter SDF = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private static final int MAX_EMBED_PLAYER_INDICATOR_LENGTH = 36;
    private static final char PLAYER_INDICATOR_FULL = '█';
    private static final char PLAYER_INDICATOR_EMPTY = '▒';

    private static final TimeUnit SEC = TimeUnit.SECONDS;
    private static final TimeUnit MILIS = TimeUnit.MILLISECONDS;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Utilities() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String convertMilisToDate(long milis) {
        return String.format("%02d:%02d:%02d", MILIS.toHours(milis),
            MILIS.toMinutes(milis) - TimeUnit.HOURS.toMinutes(MILIS.toHours(milis)),
            MILIS.toSeconds(milis) - TimeUnit.MINUTES.toSeconds(MILIS.toMinutes(milis)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String convertSecondsToMinutes(long seconds) {
        final long minutes = SEC.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(SEC.toHours(seconds));
        if (minutes == 0) {
            return String.format("%ds", SEC.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(SEC.toMinutes(seconds)));
        }
        return String.format("%02dm, %02ds", minutes,
            SEC.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(SEC.toMinutes(seconds)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String createPlayerPercentageTrack(double position, double maxDuration, int maxBlocks) {
        final double progressPerc = position / maxDuration * 100f;
        final int fullBlocksCount = (int) Math.round(maxBlocks * progressPerc / 100);
        final int emptyBlocksCount = maxBlocks - fullBlocksCount;
        return String.valueOf(PLAYER_INDICATOR_FULL).repeat(Math.max(0, fullBlocksCount)) +
            String.valueOf(PLAYER_INDICATOR_EMPTY).repeat(Math.max(0, emptyBlocksCount));
    }

    public static String createPlayerPercentageTrack(double position, double maxDuration) {
        return createPlayerPercentageTrack(position, maxDuration, MAX_EMBED_PLAYER_INDICATOR_LENGTH);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ValidateUserDetails validateUserDetails(CommandEventWrapper event, RemotePropertyHandler handler) {
        final String djRoleName = handler.getPossibleRemoteProperty(RemoteProperty.R_DJ_ROLE_NAME, event.getGuild());

        final boolean isNotOwner = !event.getAuthor().getId().equals(event.getGuild().getOwnerId());
        final boolean isNotManager = !event.getMember().hasPermission(Permission.MANAGE_SERVER);
        final boolean isNotDj = event.getMember().getRoles().stream().noneMatch(r -> r.getName().equals(djRoleName));

        return new ValidateUserDetails(isNotOwner, isNotManager, isNotDj);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Member checkIfMemberInGuildExist(CommandEventWrapper event, String id, BotConfiguration config) {
        return event.getGuild().getMembers().stream()
            .filter(m -> m.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundInGuildException(config, event));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getRichPageableTrackInfo(int index, AudioTrack track) {
        final User sender = ((Member) track.getUserData()).getUser();
        return String.format("`%d`. [ %s ], %s\n**%s**",
            index, convertMilisToDate(track.getDuration()), sender.getAsTag(), getRichTrackTitle(track.getInfo()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getRichTrackTitle(AudioTrackInfo audioTrackInfo) {
        return String.format("[%s](%s)", audioTrackInfo.title.replace("*", StringUtils.EMPTY), audioTrackInfo.uri);
    }

    public static String getRichTrackTitle(AudioTrack audioTrack) {
        final AudioTrackInfo info = audioTrack.getInfo();
        return String.format("[ %s ] : [%s](%s)", convertMilisToDate(audioTrack.getDuration()),
            info.title.replace("*", StringUtils.EMPTY), info.uri);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static TextChannel getSystemTextChannel(Guild guild) {
        final TextChannel systemChannel = guild.getSystemChannel();
        if (Objects.isNull(systemChannel)) {
            return guild.getTextChannels().get(0);
        }
        return systemChannel;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getFormattedUTCNow() {
        final OffsetDateTime nowUtc = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
        return SDF.format(nowUtc);
    }
}
