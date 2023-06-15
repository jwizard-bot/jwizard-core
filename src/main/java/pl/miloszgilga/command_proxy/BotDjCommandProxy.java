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
import pl.miloszgilga.domain.dj_commands.DjCommandEntity;

import static pl.miloszgilga.BotCommand.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotDjCommandProxy implements IBotCommandProxy<DjCommandEntity> {

    PX__SET_PLAYER_VOLUME       (SET_PLAYER_VOLUME,     DjCommandEntity::getSetvolumeEnabled,   DjCommandEntity::setSetvolumeEnabled),
    PX__RESET_PLAYER_VOLUME     (RESET_PLAYER_VOLUME,   DjCommandEntity::getVolumeclsEnabled,   DjCommandEntity::setVolumeclsEnabled),
    PX__JOIN_TO_CHANNEL         (JOIN_TO_CHANNEL,       DjCommandEntity::getJoinEnabled,        DjCommandEntity::setJoinEnabled),
    PX__REMOVE_MEMBER_TRACKS    (REMOVE_MEMBER_TRACKS,  DjCommandEntity::getTracksrmEnabled,    DjCommandEntity::setTracksrmEnabled),
    PX__SHUFFLE_QUEUE           (SHUFFLE_QUEUE,         DjCommandEntity::getShuffleEnabled,     DjCommandEntity::setShuffleEnabled),
    PX__SKIP_TO_TRACK           (SKIP_TO_TRACK,         DjCommandEntity::getSkiptoEnabled,      DjCommandEntity::setSkiptoEnabled),
    PX__SKIP_TRACK              (SKIP_TRACK,            DjCommandEntity::getSkipEnabled,        DjCommandEntity::setSkipEnabled),
    PX__CLEAR_QUEUE             (CLEAR_QUEUE,           DjCommandEntity::getClearEnabled,       DjCommandEntity::setClearEnabled),
    PX__STOP_CLEAR_QUEUE        (STOP_CLEAR_QUEUE,      DjCommandEntity::getStopEnabled,        DjCommandEntity::setStopEnabled),
    PX__MOVE_TRACK              (MOVE_TRACK,            DjCommandEntity::getMoveEnabled,        DjCommandEntity::setMoveEnabled),
    PX__INFINITE_PLAYLIST       (INFINITE_PLAYLIST,     DjCommandEntity::getInfiniteEnabled,    DjCommandEntity::setInfiniteEnabled);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotCommand command;
    private final Function<DjCommandEntity, Boolean> isEnabled;
    private final IBiSupplier<DjCommandEntity, Boolean> setValue;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getUid() {
        return String.format("DIDC-%03d", ordinal() + 1);
    }
}
