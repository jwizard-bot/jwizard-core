/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.exception

import pl.jwizard.core.command.CompoundCommandEvent

object UserException {
	class UserIsAlreadyWithBotException(event: CompoundCommandEvent) : AbstractBotException(
		event, UserIsAlreadyWithBotException::class,
		i18nLocale = I18nExceptionLocale.USER_ID_ALREADY_WITH_BOT,
		logMessage = "Attempt to invoke command, while user is together with bot"
	)

	class UserNotFoundInGuildException(event: CompoundCommandEvent) : AbstractBotException(
		event, UserNotFoundInGuildException::class,
		i18nLocale = I18nExceptionLocale.USER_NOT_FOUND_IN_GUILD,
		logMessage = "Attempt to find not existing user in selected guild"
	)

	class UserNotAddedTracksToQueueException(event: CompoundCommandEvent) : AbstractBotException(
		event, UserNotAddedTracksToQueueException::class,
		i18nLocale = I18nExceptionLocale.USER_NOT_ADDED_TRACKS_TO_QUEUE,
		logMessage = "Attempt to perform action on tracks from user which not added any track in queue"
	)

	class UnauthorizedDjOrSenderException(event: CompoundCommandEvent) : AbstractBotException(
		event, UnauthorizedDjOrSenderException::class,
		i18nLocale = I18nExceptionLocale.UNAUTHORIZED_DJ_OR_SENDER,
		logMessage = "Attempt to invoke DJ command without DJ guild role or without send all tracks"
	)

	class UnauthorizedDjException(event: CompoundCommandEvent) : AbstractBotException(
		event, UnauthorizedDjException::class,
		i18nLocale = I18nExceptionLocale.UNAUTHORIZED_DJ,
		logMessage = "Attempt to invoke DJ command without DJ guild role"
	)

	class UnauthorizedManagerException(event: CompoundCommandEvent) : AbstractBotException(
		event, UnauthorizedManagerException::class,
		i18nLocale = I18nExceptionLocale.UNAUTHORIZED_MANAGER,
		logMessage = "Attempt to invoke MANAGER role command without MANAGER guild role"
	)

	class UserOnVoiceChannelNotFoundException(event: CompoundCommandEvent) : AbstractBotException(
		event, UserOnVoiceChannelNotFoundException::class,
		i18nLocale = I18nExceptionLocale.USER_ON_VOICE_CHANNEL_NOT_FOUND,
		logMessage = "Attempt to invoke command while user is not in voice channel"
	)

	class UserOnVoiceChannelWithBotNotFoundException(event: CompoundCommandEvent) : AbstractBotException(
		event, UserOnVoiceChannelWithBotNotFoundException::class,
		i18nLocale = I18nExceptionLocale.USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND,
		logMessage = "Attempt to invoke command while user is not in voice channel with bot"
	)
}
