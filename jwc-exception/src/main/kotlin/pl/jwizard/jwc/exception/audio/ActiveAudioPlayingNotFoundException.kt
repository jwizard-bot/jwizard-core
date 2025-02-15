package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class ActiveAudioPlayingNotFoundException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.ACTIVE_AUDIO_PLAYING_NOT_FOUND,
	logMessage = "Attempt to invoke command while bot is not playing any audio content.",
)
