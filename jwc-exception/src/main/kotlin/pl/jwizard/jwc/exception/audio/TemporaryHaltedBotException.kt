package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class TemporaryHaltedBotException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.TEMPORARY_HALTED_BOT,
	logMessage = "Attempt to use music command on halted (muted) bot.",
)
