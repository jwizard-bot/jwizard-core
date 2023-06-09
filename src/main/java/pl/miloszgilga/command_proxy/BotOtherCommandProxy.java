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
import pl.miloszgilga.domain.other_commands.OtherCommandEntity;

import static pl.miloszgilga.BotCommand.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum BotOtherCommandProxy implements IBotCommandProxy {

    PX__HELP                        (HELP,                      OtherCommandEntity::getHelpEnabled,         OtherCommandEntity::setHelpEnabled),
    PX__HELP_ME                     (HELP_ME,                   OtherCommandEntity::getHelpmeEnabled,       OtherCommandEntity::setHelpmeEnabled),
    PX__DEBUG                       (DEBUG,                     OtherCommandEntity::getDebugEnabled,        OtherCommandEntity::setDebugEnabled),
    PX__SET_AUDIO_CHANNEL           (SET_AUDIO_CHANNEL,         OtherCommandEntity::getSetaudiochnEnabled,  OtherCommandEntity::setSetaudiochnEnabled),
    PX__SET_DJ_ROLE_NAME            (SET_DJ_ROLE_NAME,          OtherCommandEntity::getSetdjroleEnabled,    OtherCommandEntity::setSetdjroleEnabled),
    PX__SET_I18N_LOCALE             (SET_I18N_LOCALE,           OtherCommandEntity::getSetlangEnabled,      OtherCommandEntity::setSetlangEnabled),
    PX__SET_TRACK_REPEATS           (SET_TRACK_REPEATS,         OtherCommandEntity::getSettrackrepEnabled,  OtherCommandEntity::setSettrackrepEnabled),
    PX__SET_DEF_VOLUME              (SET_DEF_VOLUME,            OtherCommandEntity::getSetdefvolEnabled,    OtherCommandEntity::setSetdefvolEnabled),
    PX__SET_SKIP_RATIO              (SET_SKIP_RATIO,            OtherCommandEntity::getSetskratioEnabled,   OtherCommandEntity::setSetskratioEnabled),
    PX__SET_TIME_VOTING             (SET_TIME_VOTING,           OtherCommandEntity::getSettimevotEnabled,   OtherCommandEntity::setSettimevotEnabled),
    PX__SET_TIME_LEAVE_EMPTY        (SET_TIME_LEAVE_EMPTY,      OtherCommandEntity::getSettleavemEnabled,   OtherCommandEntity::setSettleavemEnabled),
    PX__SET_TIME_LEAVE_NO_TRACKS    (SET_TIME_LEAVE_NO_TRACKS,  OtherCommandEntity::getSettleavetrEnabled,  OtherCommandEntity::setSettleavetrEnabled);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final BotCommand command;
    private final Function<OtherCommandEntity, Boolean> isEnabled;
    private final IBiSupplier<OtherCommandEntity, Boolean> setValue;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getUid() {
        return String.format("RIDC-%03d", ordinal() + 1);
    }
}
