/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RemoteProperty.java
 * Last modified: 28/04/2023, 20:51
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

package pl.miloszgilga.core.remote;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.domain.guild_settings.GuildSettingsEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum RemoteProperty {

    R_TEXT_MUSIC_CHANNEL_ID             (null, false, GuildSettingsEntity::getAudioTextChannelId),
    R_DJ_ROLE_NAME                      (BotProperty.J_DJ_ROLE_NAME, true, GuildSettingsEntity::getDjRoleName),
    R_SELECTED_LOCALE                   (BotProperty.J_SELECTED_LOCALE, true, GuildSettingsEntity::getI18nLocale),
    R_VOTING_PERCENTAGE_RATIO           (BotProperty.J_VOTING_PERCENTAGE_RATIO, true, GuildSettingsEntity::getSkipRatio),
    R_DEFAULT_PLAYER_VOLUME_UNITS       (BotProperty.J_DEFAULT_PLAYER_VOLUME_UNITS, true, GuildSettingsEntity::getPlayerVolume),
    R_MAX_REPEATS_SINGLE_TRACK          (BotProperty.J_MAX_REPEATS_SINGLE_TRACK, true, GuildSettingsEntity::getMaxRepeatsSingleTrack),
    R_INACTIVITY_VOTING_TIMEOUT         (BotProperty.J_INACTIVITY_VOTING_TIMEOUT, true, GuildSettingsEntity::getTimeToEndVoting),
    R_INACTIVITY_EMPTY_TIMEOUT          (BotProperty.J_INACTIVITY_EMPTY_TIMEOUT, true, GuildSettingsEntity::getTimeToLeaveEmptyChannel),
    R_INACTIVITY_NO_TRACK_TIMEOUT       (BotProperty.J_INACTIVITY_NO_TRACK_TIMEOUT, true, GuildSettingsEntity::getTimeToLeaveNoTracksChannel);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotProperty localProperty;
    private final boolean hasLocalProperty;
    private final Function<GuildSettingsEntity, Object> remoteProp;
}
