/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.exception

import pl.jwizard.jwc.core.jda.command.CommandBaseContext

/**
 * Exception thrown when there is an issue invoking a command.
 *
 * This exception is used to signal that an error occurred during the execution of a command, providing an error
 * message and an optional context in which the command was invoked.
 *
 * @property exceptionMessage A message describing the reason for the exception.
 * @property context Optional context related to the command invocation, providing additional information about the
 *           command's execution environment.
 * @author Miłosz Gilga
 */
class CommandInvocationException(
	private val exceptionMessage: String,
	val context: CommandBaseContext? = null,
) : RuntimeException(exceptionMessage)
