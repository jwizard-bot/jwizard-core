/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.exception

import pl.jwizard.jwc.core.exception.CommandPipelineException
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext

/**
 * Exception thrown when the arguments provided to a command do not match the expected syntax.
 *
 * This exception indicates that the command invocation has failed due to a mismatch between the required argument
 * structure and what was actually provided by the user.
 *
 * @param commandBaseContext The context in which the command was invoked.
 * @param command The name of the command that was attempted to be executed.
 * @param syntax The expected syntax for the command's arguments.
 * @author Miłosz Gilga
 */
class MismatchCommandArgumentsException(
	commandBaseContext: CommandBaseContext,
	command: String,
	syntax: String,
) : CommandPipelineException(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.MISMATCH_COMMAND_ARGS,
	variables = mapOf("syntax" to syntax),
	logMessage = "Attempt to invoke command: \"$command\" with non-exact arguments.",
)
