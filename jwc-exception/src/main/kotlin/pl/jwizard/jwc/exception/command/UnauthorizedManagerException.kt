/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a non-superuser attempts to invoke a restricted command that requires elevated permissions.
 *
 * @param context The context of the command that caused the exception.
 * @author Miłosz Gilga
 */
class UnauthorizedManagerException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.UNAUTHORIZED_MANAGER,
	logMessage = "Attempt to invoke command: \"${context.commandName}\" without superuser role.",
)
