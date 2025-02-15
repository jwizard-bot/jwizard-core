package pl.jwizard.jwc.exception.user

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class UserNotFoundInGuildException(
	context: CommandBaseContext,
	userId: Long,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.USER_NOT_FOUND_IN_GUILD,
	logMessage = "Attempt to find user with ID: \"$userId\" which is not member of current guild.",
)
