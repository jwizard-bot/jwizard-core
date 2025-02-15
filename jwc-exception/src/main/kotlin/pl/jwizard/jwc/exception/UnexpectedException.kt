package pl.jwizard.jwc.exception

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class UnexpectedException(
	context: CommandBaseContext?,
	cause: String? = "Unknown command pipeline exception",
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.UNEXPECTED_EXCEPTION,
	logMessage = "Unexpected bot exception. Cause: \"$cause\"."
)
