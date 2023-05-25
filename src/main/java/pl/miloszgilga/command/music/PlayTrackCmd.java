/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlayTrackCmd.java
 * Last modified: 28/04/2023, 23:38
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

package pl.miloszgilga.command.music;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.Objects;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.BotCommandArgument;
import pl.miloszgilga.audioplayer.MusicManager;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractMusicCommand;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class PlayTrackCmd extends AbstractMusicCommand {

    PlayTrackCmd(
        BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(BotCommand.PLAY_TRACK, config, playerManager, embedBuilder, handler);
        super.onSameChannelWithBot = true;
        super.selfJoinable = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final UrlValidator urlValidator = new UrlValidator();
        String searchPhrase = event.getArgumentAndParse(BotCommandArgument.TRACK_LINK_NAME_ARG);
        boolean urlPatternValid = urlValidator.isValid(searchPhrase);
        if (urlPatternValid) {
            searchPhrase = searchPhrase.replaceAll(" ", "");
        } else {
            searchPhrase = "ytsearch: " + searchPhrase + " audio";
        }
        final MusicManager musicManager = playerManager.getMusicManager(event);
        if (!Objects.isNull(musicManager)) {
            musicManager.getTrackScheduler().setDeliveryEvent(event);
        }
        playerManager.loadAndPlay(event, searchPhrase, urlPatternValid);
    }
}
