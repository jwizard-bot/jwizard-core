package pl.jwizard.jwc.exception.user

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class UserOnVoiceChannelNotFoundException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.USER_ON_VOICE_CHANNEL_NOT_FOUND,
	logMessage = "Attempt to invoke command while user is not in voice channel.",
)
