/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractDjCommand.java
 * Last modified: 16/05/2023, 19:31
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

package pl.miloszgilga.command;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.misc.ValidateUserDetails;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.audioplayer.SchedulerActions;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.cacheable.CacheableCommandStateDao;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.CommandException.UnauthorizedDjException;
import static pl.miloszgilga.exception.CommandException.UnauthorizedDjOrSenderException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractDjCommand extends AbstractMusicCommand {

    protected boolean allowAlsoForNormal = true;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected AbstractDjCommand(
        BotCommand command, BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler, CacheableCommandStateDao cacheableCommandStateDao
    ) {
        super(command, config, playerManager, embedBuilder, handler, cacheableCommandStateDao);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final ValidateUserDetails details = Utilities.validateUserDetails(event, handler);
        final SchedulerActions actions = playerManager.getMusicManager(event).getActions();
        if (allowAlsoForNormal) {
            final boolean allFromOne = actions.checkIfAllTrackOrTracksIsFromSelectedMember(event.getMember());
            if (details.isNotOwner() && details.isNotManager() && details.isNotDj() && !allFromOne) {
                throw new UnauthorizedDjOrSenderException(config, event);
            }
        } else {
            if (details.isNotOwner() && details.isNotManager() && details.isNotDj()) {
                throw new UnauthorizedDjException(config, event);
            }
        }
        doExecuteDjCommand(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteDjCommand(CommandEventWrapper event);
}
