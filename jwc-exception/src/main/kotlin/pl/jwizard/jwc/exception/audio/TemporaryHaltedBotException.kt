/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when an attempt is made to execute a music command while the bot is temporarily halted or muted.
 *
 * @param context The context of the command that caused the exception.
 * @author Miłosz Gilga
 */
class TemporaryHaltedBotException(context: CommandBaseContext) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.TEMPORARY_HALTED_BOT,
	logMessage = "Attempt to use music command on halted (muted) bot.",
)
