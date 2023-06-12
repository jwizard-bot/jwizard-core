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
import pl.miloszgilga.domain.music_commands.MusicCommandEntity;

import static pl.miloszgilga.BotCommand.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotMusicCommandProxy implements IBotCommandProxy {

    PX__PLAY_TRACK          (PLAY_TRACK,            MusicCommandEntity::getPlayEnabled,         MusicCommandEntity::setPlayEnabled),
    PX__PAUSE_TRACK         (PAUSE_TRACK,           MusicCommandEntity::getPauseEnabled,        MusicCommandEntity::setPauseEnabled),
    PX__RESUME_TRACK        (RESUME_TRACK,          MusicCommandEntity::getResumeEnabled,       MusicCommandEntity::setResumeEnabled),
    PX__REPEAT_TRACK        (REPEAT_TRACK,          MusicCommandEntity::getRepeatEnabled,       MusicCommandEntity::setRepeatEnabled),
    PX__CLEAR_REPEAT_TRACK  (CLEAR_REPEAT_TRACK,    MusicCommandEntity::getRepeatclsEnabled,    MusicCommandEntity::setRepeatclsEnabled),
    PX__LOOP_TRACK          (LOOP_TRACK,            MusicCommandEntity::getLoopEnabled,         MusicCommandEntity::setLoopEnabled),
    PX__CURRENT_PLAYING     (CURRENT_PLAYING,       MusicCommandEntity::getPlayingEnabled,      MusicCommandEntity::setPlayingEnabled),
    PX__CURRENT_PAUSED      (CURRENT_PAUSED,        MusicCommandEntity::getPausedEnabled,       MusicCommandEntity::setPausedEnabled),
    PX__GET_PLAYER_VOLUME   (GET_PLAYER_VOLUME,     MusicCommandEntity::getGetvolumeEnabled,    MusicCommandEntity::setGetvolumeEnabled),
    PX__QUEUE               (QUEUE,                 MusicCommandEntity::getQueueEnabled,        MusicCommandEntity::setQueueEnabled);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotCommand command;
    private final Function<MusicCommandEntity, Boolean> isEnabled;
    private final IBiSupplier<MusicCommandEntity, Boolean> setValue;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getUid() {
        return String.format("MIDC-%03d", ordinal() + 1);
    }
}
