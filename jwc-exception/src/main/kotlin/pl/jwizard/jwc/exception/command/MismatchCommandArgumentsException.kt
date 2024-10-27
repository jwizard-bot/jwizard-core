/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when the arguments provided to a command do not match the expected syntax.
 *
 * This exception indicates that the command invocation has failed due to a mismatch between the required argument
 * structure and what was actually provided by the user.
 *
 * @param context The context in which the command was invoked.
 * @param syntax The expected syntax for the command's arguments.
 * @author Miłosz Gilga
 */
class MismatchCommandArgumentsException(
	context: CommandBaseContext,
	syntax: String,
) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.MISMATCH_COMMAND_ARGS,
	args = mapOf("syntax" to syntax),
	logMessage = "Attempt to invoke command: \"${context.commandName}\" with non-exact arguments.",
)
