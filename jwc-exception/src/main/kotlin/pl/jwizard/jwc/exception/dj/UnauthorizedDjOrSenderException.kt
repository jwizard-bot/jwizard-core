/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.dj

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a user attempts to invoke a DJ command without the required DJ role or the necessary
 * permissions to send all content.
 *
 * @param context The context of the command that triggered this exception.
 * @param djRoleName The name of the DJ role that is required to execute the command.
 * @author Miłosz Gilga
 */
class UnauthorizedDjOrSenderException(
	context: CommandBaseContext,
	djRoleName: String,
) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.UNAUTHORIZED_DJ_OR_SENDER,
	logMessage = "Attempt to invoke DJ command without DJ role: \"$djRoleName\" or without send all content.",
)
