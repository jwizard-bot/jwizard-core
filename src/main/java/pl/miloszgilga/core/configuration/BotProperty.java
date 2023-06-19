/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: BotProperty.java
 * Last modified: 16/05/2023, 12:34
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

package pl.miloszgilga.core.configuration;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum BotProperty {

    J_NAME                          ("name"),
    J_SOURCE_CODE_PATH              ("source-code-path"),
    J_WEBSITE_LINK                  ("website-link"),
    J_CONTRIBUTE_EMAIL              ("contribute-email"),
    J_PREFIX                        ("prefix"),
    J_HELP_EMAIL                    ("help-email"),
    J_SLASH_COMMANDS_ENABLED        ("slash-commands.enabled"),
    J_HAS_AVATAR                    ("avatar.has-avatar"),
    J_PATH_TO_AVATAR                ("avatar.path-to-avatar"),
    J_AVATAR_DAY_NIGHT_ENABLED      ("avatar.day-night-mode.enabled"),
    J_AVATAR_DAY_NIGHT_TIMEZONE     ("avatar.day-night-mode.timezone"),
    J_AVATAR_DAY_TRIGGER            ("avatar.day-night-mode.day.trigger-hour"),
    J_PATH_TO_AVATAR_DAY_MODE       ("avatar.day-night-mode.day.path-to-avatar"),
    J_AVATAR_NIGHT_TRIGGER          ("avatar.day-night-mode.night.trigger-hour"),
    J_PATH_TO_AVATAR_NIGHT_MODE     ("avatar.day-night-mode.night.path-to-avatar"),
    J_AUTH_TOKEN                    ("authorization.token",                                         true, EnvProperty.TOKEN),
    J_APP_ID                        ("authorization.application-id",                                true, EnvProperty.APP_ID),
    J_INACTIVITY_EMPTY_TIMEOUT      ("timeout.inactivity.time-after-leave-empty-channel"),
    J_INACTIVITY_NO_TRACK_TIMEOUT   ("timeout.inactivity.time-after-leave-no-track-channel"),
    J_MAX_INACTIVITY_EMPTY_TIME     ("timeout.inactivity.max-time-after-leave-empty-channel"),
    J_MAX_INACTIVITY_NO_TRACK_TIME  ("timeout.inactivity.max-time-after-leave-no-track-channel"),
    J_INACTIVITY_VOTING_TIMEOUT     ("voting.elapsed-time-after-finish"),
    J_MAX_INACTIVITY_VOTING_TIME    ("voting.max-elapsed-time-after-finish"),
    J_VOTING_PERCENTAGE_RATIO       ("voting.percentage-ratio"),
    J_SONG_CHOOSER_SELECT_TIME      ("song-chooser.elapse-time-after-autochoose"),
    J_MAX_SONG_CHOOSER_SELECT_TIME  ("song-chooser.max-elapse-time-after-autochoose"),
    J_SONG_CHOOSER_RANDOM_ACTIVE    ("song-chooser.random-autochoose"),
    J_SONG_CHOOSER_COUNT            ("song-chooser.count-songs-manualchoose"),
    J_MAX_REPEATS_SINGLE_TRACK      ("audio.max-repeats-single-track"),
    J_DEFAULT_PLAYER_VOLUME_UNITS   ("audio.default-player-volume-units"),
    J_DJ_ROLE_NAME                  ("misc.dj-role-name"),
    J_SELECTED_LOCALE               ("misc.locale.selected-locale"),
    J_AVAILABLE_LOCALES             ("misc.locale.available-locales"),
    J_RR_ACTIVITY_ENABLED           ("misc.round-robin-activity.enable-sequencer"),
    J_RR_INTERVAL                   ("misc.round-robin-activity.sequencer-inverval-seconds"),
    J_RR_RANDOMIZED                 ("misc.round-robin-activity.randomized"),
    J_RR_EXTERNAL_FILE_ENABLED      ("misc.round-robin-activity.show-from-external-file.enabled"),
    J_RR_EXTERNAL_FILE_PATH         ("misc.round-robin-activity.show-from-external-file.path-to-file"),
    J_RR_COMMANDS_ENABLED           ("misc.round-robin-activity.show-commands.enabled"),
    J_PAGINATION_MAX                ("pagination.max-elements-per-page"),
    J_PAGINATION_MENU_IS_ALIVE      ("pagination.menu-is-alive-seconds"),
    J_STATS_MODULE_ENABLED          ("modules.stats-module-enabled"),
    J_MUSIC_MODULE_ENABLED          ("modules.music-module-enabled"),
    J_PLAYLISTS_MODULE_ENABLED      ("modules.playlists-module-enabled"),
    J_VOTING_MODULE_ENABLED         ("modules.voting-module-enabled");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private boolean isEnvVariable;
    private EnvProperty envProperty;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static BotProperty getBaseName(String name) {
        return Arrays.stream(BotProperty.values())
            .filter(v -> (BotConfiguration.JPREFIX + "." + v.name).equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Property " + name + " not exist."));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static EnvPropertyHolder getEnvProperty(String name, AppMode appMode) {
        final BotProperty botProperty = getBaseName(name);
        if (!botProperty.isEnvVariable) return null;
        final String placeholder = "${env:" + appMode.getMode() + "_" + botProperty.getEnvProperty().getName() + "}";
        return new EnvPropertyHolder(placeholder, appMode.getMode() + "_" + botProperty.envProperty.getName());
    }
}
