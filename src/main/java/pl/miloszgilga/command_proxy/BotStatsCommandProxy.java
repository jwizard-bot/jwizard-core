/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotCommandProxy.java
 * Last modified: 6/8/23, 9:33 PM
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

package pl.miloszgilga.command_proxy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.domain.stats_commands.StatsCommandEntity;

import static pl.miloszgilga.BotCommand.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotStatsCommandProxy implements IBotCommandProxy<StatsCommandEntity> {

    PX__GUILD_STATS         (GUILD_STATS,           StatsCommandEntity::getGstatsEnabled,       StatsCommandEntity::setGstatsEnabled),
    PX__MEMBER_STATS        (MEMBER_STATS,          StatsCommandEntity::getMstatsEnabled,       StatsCommandEntity::setMstatsEnabled),
    PX__MY_STATS            (MY_STATS,              StatsCommandEntity::getMystatsEnabled,      StatsCommandEntity::setMystatsEnabled),
    PX__ENABLE_STATS        (ENABLE_STATS,          StatsCommandEntity::getStatsonEnabled,      StatsCommandEntity::setStatsonEnabled),
    PX__DISABLE_STATS       (DISABLE_STATS,         StatsCommandEntity::getStatsoffEnabled,     StatsCommandEntity::setStatsoffEnabled),
    PX__PUBLIC_STATS        (PUBLIC_STATS,          StatsCommandEntity::getPubstatsEnabled,     StatsCommandEntity::setPubstatsEnabled),
    PX__PRIVATE_STATS       (PRIVATE_STATS,         StatsCommandEntity::getPrivstatsEnabled,    StatsCommandEntity::setPrivstatsEnabled),
    PX__RESET_MEMBER_STATS  (RESET_MEMBER_STATS,    StatsCommandEntity::getResetmstatsEnabled,  StatsCommandEntity::setResetmstatsEnabled),
    PX__RESET_GUILD_STATS   (RESET_GUILD_STATS,     StatsCommandEntity::getResetgstatsEnabled,  StatsCommandEntity::setResetgstatsEnabled);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotCommand command;
    private final Function<StatsCommandEntity, Boolean> isEnabled;
    private final IBiSupplier<StatsCommandEntity, Boolean> setValue;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getUid() {
        return String.format("SIDC-%03d", ordinal() + 1);
    }

    @Override
    public String getCacheProxyName() {
        return "GuildStatsCommandsStateCache";
    }
}
