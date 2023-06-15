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
import pl.miloszgilga.domain.vote_commands.VoteCommandEntity;

import static pl.miloszgilga.BotCommand.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotVoteCommandProxy implements IBotCommandProxy<VoteCommandEntity> {

    PX__VOTE_SHUFFLE_QUEUE      (VOTE_SHUFFLE_QUEUE,        VoteCommandEntity::getVshuffleEnabled,  VoteCommandEntity::setVshuffleEnabled),
    PX__SKIP_TRACK              (SKIP_TRACK,                VoteCommandEntity::getVskipEnabled,     VoteCommandEntity::setVskipEnabled),
    PX__VOTE_SKIP_TO_TRACK      (VOTE_SKIP_TO_TRACK,        VoteCommandEntity::getVskiptoEnabled,   VoteCommandEntity::setVskiptoEnabled),
    PX__VOTE_CLEAR_QUEUE        (VOTE_CLEAR_QUEUE,          VoteCommandEntity::getVclearEnabled,    VoteCommandEntity::setVclearEnabled),
    PX__VOTE_STOP_CLEAR_QUEUE   (VOTE_STOP_CLEAR_QUEUE,     VoteCommandEntity::getVstopEnabled,     VoteCommandEntity::setVstopEnabled);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotCommand command;
    private final Function<VoteCommandEntity, Boolean> isEnabled;
    private final IBiSupplier<VoteCommandEntity, Boolean> setValue;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getUid() {
        return String.format("VIDC-%03d", ordinal() + 1);
    }
}
