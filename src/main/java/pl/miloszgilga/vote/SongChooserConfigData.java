/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SongChooserConfigData.java
 * Last modified: 18/06/2023, 17:59
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

package pl.miloszgilga.vote;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.function.Consumer;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record SongChooserConfigData(
    BotConfiguration config,
    CommandEventWrapper event,
    RemotePropertyHandler handler,
    EmbedMessageBuilder builder,
    Consumer<AudioTrack> onSelectTrack
) {
}
