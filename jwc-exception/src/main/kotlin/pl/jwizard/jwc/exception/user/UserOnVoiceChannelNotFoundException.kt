/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.user

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when a command is invoked while the user is not connected to any voice channel.
 *
 * @param commandBaseContext The context of the command that caused the exception.
 * @author Miłosz Gilga
 */
class UserOnVoiceChannelNotFoundException(
	commandBaseContext: CommandBaseContext,
) : CommandPipelineExceptionHandler(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.USER_ON_VOICE_CHANNEL_NOT_FOUND,
	logMessage = "Attempt to invoke command while user is not in voice channel.",
)
