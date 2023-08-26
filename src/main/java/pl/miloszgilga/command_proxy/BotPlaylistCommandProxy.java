/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotPlaylistCommandProxy.java
 * Last modified: 20/06/2023, 16:34
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

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.domain.playlist_commands.PlaylistCommandEntity;

import java.util.function.Function;

import static pl.miloszgilga.BotCommand.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotPlaylistCommandProxy implements IBotCommandProxy<PlaylistCommandEntity> {

    PX__ADD_FROM_FILE_PLAYLIST  (ADD_FROM_FILE_PLAYLIST,    PlaylistCommandEntity::getAddplfileEnabled,     PlaylistCommandEntity::setAddplfileEnabled),
    PX__ADD_TRACK_TO_PLAYLIST   (ADD_TRACK_TO_PLAYLIST,     PlaylistCommandEntity::getAddpltrackEnabled,    PlaylistCommandEntity::setAddpltrackEnabled),
    PX__CHANGE_PLAYLIST_NAME    (CHANGE_PLAYLIST_NAME,      PlaylistCommandEntity::getModplnameEnabled,     PlaylistCommandEntity::setModplnameEnabled),
    PX__CLEAR_PLAYLIST          (CLEAR_PLAYLIST,            PlaylistCommandEntity::getClrplaylistEnabled,   PlaylistCommandEntity::setClrplaylistEnabled),
    PX__CREATE_PLAYLIST         (CREATE_PLAYLIST,           PlaylistCommandEntity::getAddplaylistEnabled,   PlaylistCommandEntity::setAddplaylistEnabled),
    PX__PLAY_FROM_PLAYLIST      (PLAY_FROM_PLAYLIST,        PlaylistCommandEntity::getPlayplEnabled,        PlaylistCommandEntity::setPlayplEnabled),
    PX__MOVE_TRACK_IN_PLAYLIST  (MOVE_TRACK_IN_PLAYLIST,    PlaylistCommandEntity::getMvtrackplEnabled,     PlaylistCommandEntity::setMvtrackplEnabled),
    PX__REMOVE_PLAYLIST         (REMOVE_PLAYLIST,           PlaylistCommandEntity::getRmplaylistEnabled,    PlaylistCommandEntity::setRmplaylistEnabled),
    PX__REMOVE_TRACK_PLAYLIST   (REMOVE_TRACK_PLAYLIST,     PlaylistCommandEntity::getRmtrackplEnabled,     PlaylistCommandEntity::setRmtrackplEnabled),
    PX__SAVE_TRACK_PLAYLIST     (SAVE_TRACK_PLAYLIST,       PlaylistCommandEntity::getSavetrackplEnabled,   PlaylistCommandEntity::setSavetrackplEnabled),
    PX__SET_PLAYLIST_VISIBILITY (SET_PLAYLIST_VISIBILITY,   PlaylistCommandEntity::getPlvisiblityEnabled,   PlaylistCommandEntity::setPlvisiblityEnabled),
    PX__SHOW_MEMBER_PLAYLISTS   (SHOW_MEMBER_PLAYLISTS,     PlaylistCommandEntity::getShowmemplEnabled,     PlaylistCommandEntity::setShowmemplEnabled),
    PX__SHOW_MY_PLAYLISTS       (SHOW_MY_PLAYLISTS,         PlaylistCommandEntity::getShowmyplEnabled,      PlaylistCommandEntity::setShowmyplEnabled),
    PX__SHOW_PLAYLIST_CONTENT   (SHOW_PLAYLIST_CONTENT,     PlaylistCommandEntity::getShowplsongsEnabled,   PlaylistCommandEntity::setShowplsongsEnabled);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotCommand command;
    private final Function<PlaylistCommandEntity, Boolean> isEnabled;
    private final IBiSupplier<PlaylistCommandEntity, Boolean> setValue;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getUid() {
        return String.format("PIDC-%03d", ordinal() + 1);
    }

    @Override
    public String getCacheProxyName() {
        return "GuildPlaylistsCommandsStateCache";
    }

}
