package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class DiscreteAudioStreamIsPlayingException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.DISCRETE_AUDIO_STREAM_IS_PLAYING,
	args = mapOf("stopCmd" to Command.STOP.parseWithPrefix(context)),
	logMessage = "Attempt to invoke radio command, while non-continuous audio stream is active.",
)
