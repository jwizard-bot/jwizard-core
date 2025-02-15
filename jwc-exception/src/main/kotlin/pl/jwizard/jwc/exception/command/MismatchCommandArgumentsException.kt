package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class MismatchCommandArgumentsException(
	context: CommandBaseContext,
	syntax: String,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.MISMATCH_COMMAND_ARGS,
	args = mapOf("syntax" to syntax),
	logMessage = "Attempt to invoke command: \"${context.commandName}\" with non-exact arguments.",
)
