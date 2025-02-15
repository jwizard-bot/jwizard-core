package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class InvokerIsNotSenderOrSuperuserException(
	context: CommandBaseContext,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.INVOKER_IS_NOT_SENDER_OR_SUPERUSER,
	logMessage = """
		Attempt to invoke action while invoker is not sender or super-user (moderator, owner or dj).
	""",
)
