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
import pl.miloszgilga.domain.owner_commands.OwnerCommandEntity;

import static pl.miloszgilga.BotCommand.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotOwnerCommandProxy implements IBotCommandProxy {

    PX__TURN_ON_STATS_MODULE        (TURN_ON_STATS_MODULE,      OwnerCommandEntity::getOnstatsmEnabled,     OwnerCommandEntity::setOnstatsmEnabled),
    PX__TURN_OFF_STATS_MODULE       (TURN_OFF_STATS_MODULE,     OwnerCommandEntity::getOffstatsmEnabled,    OwnerCommandEntity::setOffstatsmEnabled),
    PX__TURN_ON_MUSIC_MODULE        (TURN_ON_MUSIC_MODULE,      OwnerCommandEntity::getOnmusicmEnabled,     OwnerCommandEntity::setOnmusicmEnabled),
    PX__TURN_OFF_MUSIC_MODULE       (TURN_OFF_MUSIC_MODULE,     OwnerCommandEntity::getOffmusicmEnabled,    OwnerCommandEntity::setOffmusicmEnabled),
    PX__TURN_ON_PLAYLISTS_MODULE    (TURN_ON_PLAYLISTS_MODULE,  OwnerCommandEntity::getOnplaylmEnabled,     OwnerCommandEntity::setOnplaylmEnabled),
    PX__TURN_OFF_PLAYLISTS_MODULE   (TURN_OFF_PLAYLISTS_MODULE, OwnerCommandEntity::getOffplaylmEnabled,    OwnerCommandEntity::setOffplaylmEnabled),
    PX__TURN_ON_VOTING_MODULE       (TURN_ON_VOTING_MODULE,     OwnerCommandEntity::getOnvotingmEnabled,    OwnerCommandEntity::setOnvotingmEnabled),
    PX__TURN_OFF_VOTING_MODULE      (TURN_OFF_VOTING_MODULE,    OwnerCommandEntity::getOffvotingmEnabled,   OwnerCommandEntity::setOffvotingmEnabled),
    PX__TURN_ON_COMMAND             (TURN_ON_COMMAND,           OwnerCommandEntity::getCommandonEnabled,    OwnerCommandEntity::setCommandonEnabled),
    PX__TURN_OFF_COMMAND            (TURN_OFF_COMMAND,          OwnerCommandEntity::getCommandoffEnabled,   OwnerCommandEntity::setCommandoffEnabled);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotCommand command;
    private final Function<OwnerCommandEntity, Boolean> isEnabled;
    private final IBiSupplier<OwnerCommandEntity, Boolean> setValue;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getUid() {
        return String.format("OIDC-%03d", ordinal() + 1);
    }
}
