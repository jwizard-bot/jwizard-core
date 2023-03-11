/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioPlayerException.java
 * Last modified: 04/03/2023, 23:59
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.exception;

import lombok.extern.slf4j.Slf4j;

import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class AudioPlayerException {

    @Slf4j public static class UserOnVoiceChannelNotFoundException extends BotException {
        public UserOnVoiceChannelNotFoundException(BotConfiguration config, EventWrapper event) {
            super(config, LocaleSet.USER_ON_VOICE_CHANNEL_NOT_FOUND_EXEC, BugTracker.USER_ON_VOICE_CHANNEL_NOT_FOUND);
            log.error("G: {}, A: {} <> Attempt to invoke command while user is not in voice channel with bot",
                event.guildName(), event.authorTag());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TrackIsNotPlayingException extends BotException {
        public TrackIsNotPlayingException(BotConfiguration config, EventWrapper event) {
            super(config, LocaleSet.TRACK_IS_NOT_PLAYING_EXC, BugTracker.TRACK_IS_NOT_PLAYING);
            log.error("G: {}, A: {} <> Attempt to invoke command while current played track not existing",
                event.guildName(), event.authorTag());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TrackIsNotPausedException extends BotException {
        public TrackIsNotPausedException(BotConfiguration config, EventWrapper event) {
            super(config, LocaleSet.TRACK_IS_NOT_PAUSED_EXC, BugTracker.TRACK_IS_NOT_PAUSED);
            log.error("G: {}, A: {} <> Attempt to invoke command while current played track is not paused",
                event.guildName(), event.authorTag());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class InvokerIsNotTrackSenderOrAdmin extends BotException {
        public InvokerIsNotTrackSenderOrAdmin(BotConfiguration config, EventWrapper event) {
            super(config, LocaleSet.INVOKER_IS_NOT_TRACK_SENDER_OR_ADMIN_EXC, BugTracker.INVOKE_FORBIDDEN_COMMAND);
            log.error("G: {}, A: {} <> Attempt to invoke command while bot is used on another channel",
                event.guildName(), event.authorTag());
        }
    }
}
