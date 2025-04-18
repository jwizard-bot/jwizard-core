package pl.jwizard.jwc.exception.dj

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class UnauthorizedDjException(
	context: CommandBaseContext,
	djRoleName: String,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.UNAUTHORIZED_DJ,
	logMessage = "Attempt to invoke DJ command without DJ role: \"$djRoleName\".",
)
