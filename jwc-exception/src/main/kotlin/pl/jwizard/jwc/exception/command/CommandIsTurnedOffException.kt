/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when an attempt is made to execute a command that is turned off.
 *
 * This exception indicates that a command cannot be executed because it is currently disabled. It provides context
 * about the command that was attempted to be executed.
 *
 * @param context The context of the command invocation, providing details about the command's execution environment.
 * @author Miłosz Gilga
 */
class CommandIsTurnedOffException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.COMMAND_IS_TURNED_OFF,
	args = mapOf("command" to "${context.prefix}${context.commandName}"),
	logMessage = "Attempt to execute turned off command. Command: \"${context.commandName}\".",
)
