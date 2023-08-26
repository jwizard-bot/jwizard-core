/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ArgSyntaxLocaleSet.java
 * Last modified: 17/05/2023, 01:26
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

package pl.miloszgilga.locale;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import pl.miloszgilga.core.IEnumerableLocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.core.configuration.BotProperty.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum ArgSyntaxLocaleSet implements IEnumerableLocaleSet {

    PLAY_TRACK_ARG_SYNTAX                           ("jwizard.command.arguments.PlayTrack"),
    REPEAT_TRACK_ARG_SYNTAX                         ("jwizard.command.arguments.RepeatTrack"),
    AUDIO_PLAYER_SET_VOLUME_ARG_SYNTAX              ("jwizard.command.arguments.AudioPlayerSetVolume"),
    MEMBER_TAG_ARG_SYNTAX                           ("jwizard.command.arguments.MemberTag"),
    PLAYLIST_NAME_ARG_SYNTAX                        ("jwizard.command.arguments.PlaylistName"),
    PLAYLIST_ID_OR_NAME_WITH_NEW_NAME_ARG_SYNTAX    ("jwizard.command.arguments.PlaylistIdOrNameWithNewName"),
    PLAYLIST_ID_OR_NAME_WITH_TRACK_URL_SYNTAX       ("jwizard.command.arguments.PlaylistIdOrNameWithTrackUrl"),
    PLAYLIST_ID_OR_NAME_ARG_SYNTAX                  ("jwizard.command.arguments.PlaylistIdOrName"),
    PLAYLIST_ID_OR_NAME_WITH_POS_ARG_SYNTAX         ("jwizard.command.arguments.PlaylistIdOrNameWithPos"),
    PLAYLIST_ID_OR_NAME_WITH_VISIBILITY_ARG_SYNTAX  ("jwizard.command.arguments.PlaylistIdOrNameWithVisibility"),
    PLAYLIST_ID_OR_NAME_WITH_TRACK_ID_ARG_SYNTAX    ("jwizard.command.arguments.PlaylistIdOrNameWithTrackId"),
    SKIP_QUEUE_TO_TRACK_ARG_SYNTAX                  ("jwizard.command.arguments.SkipQueueToTrack"),
    MOVE_TRACK_ARG_SYNTAX                           ("jwizard.command.arguments.MoveTrack"),
    COMMAND_NAME_OR_ALIAS_ARG_SYNTAX                ("jwizard.command.arguments.CommandNameOrAlias"),
    OPTIONAL_CHANNEL_ID_ARG_SYNTAX                  ("jwizard.command.arguments.OptionalChannelId"),
    OPTIONAL_DJ_ROLE_NAME_ARG_SYNTAX                ("jwizard.command.arguments.OptionalDjRoleName"),
    OPTIONAL_I18N_LOCALE_ARG_SYNTAX                 ("jwizard.command.arguments.OptionalI18nLocale",        Map.of("langsArray", J_AVAILABLE_LOCALES)),
    OPTIONAL_TRACK_REPEATS_ARG_SYNTAX               ("jwizard.command.arguments.OptionalTrackRepeats",      Map.of("maxRepeats", J_MAX_REPEATS_SINGLE_TRACK)),
    OPTIONAL_DEFAULT_VOLUME_ARG_SYNTAX              ("jwizard.command.arguments.OptionalDefaultVolume"),
    OPTIONAL_SKIP_RATIO_ARG_SYNTAX                  ("jwizard.command.arguments.OptionalSkipRatio"),
    OPTIONAL_TIME_VOTING_ARG_SYNTAX                 ("jwizard.command.arguments.OptionalTimeSeconds",       Map.of("maxSeconds", J_MAX_INACTIVITY_VOTING_TIME)),
    OPTIONAL_TIME_LEAVE_EMPTY_ARG_SYNTAX            ("jwizard.command.arguments.OptionalTimeSeconds",       Map.of("maxSeconds", J_MAX_INACTIVITY_EMPTY_TIME)),
    OPTIONAL_TIME_LEAVE_NO_TRACKS_ARG_SYNTAX        ("jwizard.command.arguments.OptionalTimeSeconds",       Map.of("maxSeconds", J_MAX_INACTIVITY_NO_TRACK_TIME)),
    OPTIONAL_TIME_CHOOSE_SONG_ARG_SYNTAX            ("jwizard.command.arguments.OptionalTimeSeconds",       Map.of("maxSeconds", J_MAX_SONG_CHOOSER_SELECT_TIME)),
    OPTIONAL_SONG_CHOOSER_RANDOM_ARG_SYNTAX         ("jwizard.command.arguments.OptionalTrueFalse"),
    OPTIONAL_SONG_CHOOSER_COUNT_ARG_SYNTAX          ("jwizard.command.arguments.OptionalSongCount");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
    private Map<String, BotProperty> placeholders;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ArgSyntaxLocaleSet(String holder) {
        this.holder = holder;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String parse(BotConfiguration config) {
        if (Objects.isNull(placeholders)) {
            return config.getLocaleText(this);
        }
        final Map<String, Object> replacedPlaceholders = new HashMap<>();
        for (final Map.Entry<String, BotProperty> placeholder : placeholders.entrySet()) {
            replacedPlaceholders.put(placeholder.getKey(), config.getProperty(placeholder.getValue()));
        }
        return config.getLocaleText(this, replacedPlaceholders);
    }
}
