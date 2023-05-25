/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RemoteProperty.java
 * Last modified: 16/05/2023, 19:55
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
