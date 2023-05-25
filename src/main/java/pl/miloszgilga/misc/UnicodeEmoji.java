/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: UnicodeEmoji.java
 * Last modified: 04/04/2023, 04:07
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

package pl.miloszgilga.misc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.MessageReaction;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum UnicodeEmoji {
    THUMBS_UP               ("\uD83D\uDC4D"),
    THUMBS_DOWN             ("\uD83D\uDC4E");

    // https://unicode.org/emoji/charts/full-emoji-list.html#1f44d

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String code;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkEquals(MessageReaction.ReactionEmote emote) {
        return code.equals(emote.getEmoji());
    }
}
