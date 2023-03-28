/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AudioPlayerException.java
 * Last modified: 19/03/2023, 23:01
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import pl.miloszgilga.misc.JDALog;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class AudioPlayerException {

    @Slf4j public static class ActiveMusicPlayingNotFoundException extends BotException {
        public ActiveMusicPlayingNotFoundException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.ACTIVE_MUSIC_PLAYING_NOT_FOUND_EXC, BugTracker.ACTIVE_MUSIC_PLAYING_NOT_FOUND);
            JDALog.error(log, event, "Attempt to invoke command while user is not in any voice channel");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UserOnVoiceChannelNotFoundException extends BotException {
        public UserOnVoiceChannelNotFoundException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.USER_ON_VOICE_CHANNEL_NOT_FOUND_EXEC, BugTracker.USER_ON_VOICE_CHANNEL_NOT_FOUND);
            JDALog.error(log, event, "Attempt to invoke command while user is not in voice channel");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UserOnVoiceChannelWithBotNotFoundException extends BotException {
        public UserOnVoiceChannelWithBotNotFoundException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND_EXEC, BugTracker.USER_ON_VOICE_CHANNEL_NOT_FOUND);
            JDALog.error(log, event, "Attempt to invoke command while user is not in voice channel with bot");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TrackIsNotPlayingException extends BotException {
        public TrackIsNotPlayingException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.TRACK_IS_NOT_PLAYING_EXC, BugTracker.TRACK_IS_NOT_PLAYING);
            JDALog.error(log, event, "Attempt to invoke command while current played track not existing");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TrackIsNotPausedException extends BotException {
        public TrackIsNotPausedException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.TRACK_IS_NOT_PAUSED_EXC, BugTracker.TRACK_IS_NOT_PAUSED);
            JDALog.error(log, event, "Attempt to invoke command while current played track is not paused");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class InvokerIsNotTrackSenderOrAdminException extends BotException {
        public InvokerIsNotTrackSenderOrAdminException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.INVOKER_IS_NOT_TRACK_SENDER_OR_ADMIN_EXC, BugTracker.INVOKE_FORBIDDEN_COMMAND);
            JDALog.error(log, event, "Attempt to invoke command while bot is used on another channel");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TrackRepeatsOutOfBoundsException extends BotException {
        public TrackRepeatsOutOfBoundsException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.TRACK_REPEATS_OUT_OF_BOUNDS_EXC, Map.of(
                "topLimit", config.getProperty(BotProperty.J_MAX_REPEATS_SINGLE_TRACK)
            ), BugTracker.REPEATS_OUT_OF_BOUNDS);
            JDALog.error(log, event, "Attempt to set out of bounds current audio track repeats number");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class LockCommandOnTemporaryHaltedException extends BotException {
        public LockCommandOnTemporaryHaltedException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.LOCK_COMMAND_ON_TEMPORARY_HALTED_EXC, BugTracker.LOCK_COMMAND_TEMPORARY_HALTED);
            JDALog.error(log, event, "Attempt to use music command on halted (muted) bot");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class VolumeUnitsOutOfBoundsException extends BotException {
        public VolumeUnitsOutOfBoundsException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.VOLUME_UNITS_OUT_OF_BOUNDS_EXC, BugTracker.VOLUME_UNITS_OUT_OF_BOUNDS);
            JDALog.error(log, event, "Attempt to set out of bounds audio player volume units");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TrackPositionOutOfBoundsException extends BotException {
        public TrackPositionOutOfBoundsException(BotConfiguration config, CommandEventWrapper event, int maxOffset) {
            super(config, LocaleSet.TRACK_OFFSET_OUT_OF_BOUNDS_EXC, Map.of(
                "maxOffset", maxOffset
            ), BugTracker.TRACK_OFFSET_OUT_OF_BOUNDS);
            JDALog.error(log, event, "Attempt to offset to out of bounds track position in queue");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class TrackQueueIsEmptyException extends BotException {
        public TrackQueueIsEmptyException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.TRACK_QUEUE_IS_EMPTY_EXC, BugTracker.TRACK_QUEUE_IS_EMPTY);
            JDALog.error(log, event, "Attempt to use command on empty track queue");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Slf4j public static class UserNotAddedTracksToQueueException extends BotException {
        public UserNotAddedTracksToQueueException(BotConfiguration config, CommandEventWrapper event) {
            super(config, LocaleSet.USER_NOT_ADDED_TRACKS_TO_QUEUE_EXC, BugTracker.USER_NOT_ADDED_TRACKS_TO_QUEUE);
            JDALog.error(log, event, "Attempt to remove tracks from user which not added any track in queue");
        }
    }
}
