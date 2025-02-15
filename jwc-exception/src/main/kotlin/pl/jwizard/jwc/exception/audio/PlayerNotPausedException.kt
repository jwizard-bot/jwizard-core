package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class PlayerNotPausedException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.PLAYER_NOT_PAUSED,
	logMessage = "Attempt to invoke command while audio player is not paused.",
)
