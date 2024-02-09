/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.exception

import pl.jwizard.core.command.CompoundCommandEvent

object AudioPlayerException {
	class TrackQueueIsEmptyException(event: CompoundCommandEvent) : AbstractBotException(
		event, TrackQueueIsEmptyException::class,
		i18nLocale = I18nExceptionLocale.TRACK_QUEUE_IS_EMPTY,
		logMessage = "Attempt to perform action on empty track queue"
	)

	class TrackPositionsIsTheSameException(event: CompoundCommandEvent) : AbstractBotException(
		event, TrackPositionsIsTheSameException::class,
		i18nLocale = I18nExceptionLocale.TRACK_THE_SAME_POSITION,
		logMessage = "Attempt to move track to the same origin position"
	)

	class TrackPositionOutOfBoundsException(event: CompoundCommandEvent, maxOffset: Int) : AbstractBotException(
		event, TrackPositionOutOfBoundsException::class,
		i18nLocale = I18nExceptionLocale.TRACK_OFFSET_OUT_OF_BOUNDS,
		variables = mapOf("maxOffset" to maxOffset),
		logMessage = "Attempt to offset to out of bounds track position in queue"
	)

	class TrackIsNotPlayingException(event: CompoundCommandEvent) : AbstractBotException(
		event, TrackIsNotPlayingException::class,
		i18nLocale = I18nExceptionLocale.TRACK_IS_NOT_PLAYING,
		logMessage = "Attempt to invoke command while current played track not existing"
	)

	class InvokerIsNotTrackSenderOrAdminException(event: CompoundCommandEvent) : AbstractBotException(
		event, InvokerIsNotTrackSenderOrAdminException::class,
		i18nLocale = I18nExceptionLocale.INVOKER_IS_NOT_TRACK_SENDER_OR_ADMIN,
		logMessage = "Attempt to invoke action while invoker is not track sender or manager (moderator, owner or dj)"
	)

	class ForbiddenTextChannelException(event: CompoundCommandEvent, textChannelName: String) : AbstractBotException(
		event, ForbiddenTextChannelException::class,
		i18nLocale = I18nExceptionLocale.FORBIDDEN_TEXT_CHANNEL,
		variables = mapOf("acceptTextChannel" to textChannelName),
		logMessage = "Attempt to use song request command on fobidden channel. Accepted channel: $textChannelName"
	)

	class LockCommandOnTemporaryHaltedException(event: CompoundCommandEvent) : AbstractBotException(
		event, LockCommandOnTemporaryHaltedException::class,
		i18nLocale = I18nExceptionLocale.LOCK_COMMAND_ON_TEMPORARY_HALTED,
		logMessage = "Attempt to use music command on halted (muted) bot"
	)

	class ActiveMusicPlayingNotFoundException(event: CompoundCommandEvent) : AbstractBotException(
		event, ActiveMusicPlayingNotFoundException::class,
		i18nLocale = I18nExceptionLocale.ACTIVE_MUSIC_PLAYING_NOT_FOUND,
		logMessage = "Attempt to invoke command while user is not in any voice channel"
	)

	class TrackIsNotPausedException(event: CompoundCommandEvent) : AbstractBotException(
		event, TrackIsNotPausedException::class,
		i18nLocale = I18nExceptionLocale.TRACK_IS_NOT_PAUSED,
		logMessage = "Attempt to invoke command while current played track is not paused"
	)

	class TrackRepeatsOutOfBoundsException(event: CompoundCommandEvent, topLimit: Int) : AbstractBotException(
		event, TrackRepeatsOutOfBoundsException::class,
		i18nLocale = I18nExceptionLocale.TRACK_REPEATS_OUT_OF_BOUNDS,
		variables = mapOf("topLimit" to topLimit),
		logMessage = "Attempt to set out of bounds current audio track repeats number: $topLimit"
	)

	class MaxRepeatsOutOfBoundsException(event: CompoundCommandEvent, maxRepeats: Int) : AbstractBotException(
		event, MaxRepeatsOutOfBoundsException::class,
		i18nLocale = I18nExceptionLocale.MAX_REPEATS_OUT_OF_BOUNDS,
		variables = mapOf("maxRepeatsCount" to maxRepeats),
		logMessage = "Attempt to set max repeats greater than default max repeats: $maxRepeats"
	)
}
