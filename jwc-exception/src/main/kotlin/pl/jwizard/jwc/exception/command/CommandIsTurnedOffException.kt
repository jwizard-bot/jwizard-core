package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class CommandIsTurnedOffException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.COMMAND_IS_TURNED_OFF,
	args = mapOf("command" to "${context.prefix}${context.commandName}"),
	logMessage = "Attempt to execute turned off command. Command: \"${context.commandName}\".",
)
