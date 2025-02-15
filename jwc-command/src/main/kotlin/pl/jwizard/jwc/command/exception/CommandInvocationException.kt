package pl.jwizard.jwc.command.exception

import pl.jwizard.jwc.core.jda.command.CommandBaseContext

internal class CommandInvocationException(
	exceptionMessage: String,
	val context: CommandBaseContext? = null,
) : RuntimeException(exceptionMessage)
