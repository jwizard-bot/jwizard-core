/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.command

import net.dv8tion.jda.api.entities.User
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when a non-superuser attempts to invoke a restricted command that requires elevated permissions.
 *
 * @param commandBaseContext The context of the command that caused the exception.
 * @param commandName The name of the command that was attempted.
 * @param user The user who attempted to invoke the restricted command.
 * @author Miłosz Gilga
 */
class UnauthorizedManagerException(
	commandBaseContext: CommandBaseContext,
	commandName: String,
	user: User?,
) : CommandPipelineExceptionHandler(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.UNAUTHORIZED_MANAGER,
	logMessage = "Attempt to invoke command: \"$commandName\" by: \"${user?.qualifier}\" without superuser role."
)
