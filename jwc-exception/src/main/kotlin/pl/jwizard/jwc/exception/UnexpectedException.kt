/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext

/**
 * Represents an unexpected exception that occurs during command processing.
 *
 * This exception is thrown when an unforeseen error occurs, allowing for centralized logging and handling of unexpected
 * situations.
 *
 * @param context The context in which the command was executed. This may include information about the guild, user, and
 *        command properties.
 * @param cause An optional string describing the cause of the exception. Defaults to "Unknown command pipeline
 *        exception" if not provided.
 * @author Miłosz Gilga
 */
class UnexpectedException(
	context: CommandBaseContext?,
	cause: String? = "Unknown command pipeline exception",
) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.UNEXPECTED_EXCEPTION,
	logMessage = "Unexpected bot exception. Cause: \"$cause\"."
)
