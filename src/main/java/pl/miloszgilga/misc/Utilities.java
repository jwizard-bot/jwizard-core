/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: Utilities.java
 * Last modified: 19/03/2023, 23:18
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

package pl.miloszgilga.misc;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.concurrent.TimeUnit;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.CommandException.UserNotFoundInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public final class Utilities {

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

    public static ValidateUserDetails validateUserDetails(CommandEventWrapper event, BotConfiguration config) {
        final String djRoleName = config.getProperty(BotProperty.J_DJ_ROLE_NAME);

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
            .orElseThrow(() -> { throw new UserNotFoundInGuildException(config, event); });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getRichPageableTrackInfo(int index, AudioTrack track) {
        final User sender = ((Member) track.getUserData()).getUser();
        return String.format("`%d`. [ %s ], %s\n**%s**",
            index, convertMilisToDate(track.getDuration()), sender.getAsTag(), getRichTrackTitle(track.getInfo()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getRichTrackTitle(AudioTrackInfo audioTrackInfo) {
        return String.format("[%s](%s)", audioTrackInfo.title, audioTrackInfo.uri);
    }
}
