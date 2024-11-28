/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.dj

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a user attempts to invoke a DJ command without the required DJ role.
 *
 * @param context The context of the command that triggered this exception.
 * @param djRoleName The name of the missing DJ role required to execute the command.
 * @author Miłosz Gilga
 */
class UnauthorizedDjException(
	context: CommandBaseContext,
	djRoleName: String,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.UNAUTHORIZED_DJ,
	logMessage = "Attempt to invoke DJ command without DJ role: \"$djRoleName\".",
)
