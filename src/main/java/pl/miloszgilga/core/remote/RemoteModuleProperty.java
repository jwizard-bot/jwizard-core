/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RemoteModuleProperty.java
 * Last modified: 28/04/2023, 21:00
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
import pl.miloszgilga.domain.guild_modules.GuildModulesEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum RemoteModuleProperty {

    R_STATS_MODULE_ENABLED          (BotProperty.J_STATS_MODULE_ENABLED, GuildModulesEntity::getStatsModuleEnabled),
    R_MUSIC_MODULE_ENABLED          (BotProperty.J_MUSIC_MODULE_ENABLED, GuildModulesEntity::getMusicModuleEnabled),
    R_PLAYLISTS_MODULE_ENABLED      (BotProperty.J_PLAYLISTS_MODULE_ENABLED, GuildModulesEntity::getPlaylistsModuleEnabled),
    R_VOTING_MODULE_ENABLED         (BotProperty.J_VOTING_MODULE_ENABLED, GuildModulesEntity::getVotingModuleEnabled);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotProperty localProperty;
    private final Function<GuildModulesEntity, Boolean> remoteProp;
}
