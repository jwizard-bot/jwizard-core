/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a radio command is invoked while a discrete (non-continuous) audio stream is playing.
 *
 * @param context The context of the command that caused the exception.
 * @author Miłosz Gilga
 */
class DiscreteAudioStreamIsPlayingException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.DISCRETE_AUDIO_STREAM_IS_PLAYING,
	args = mapOf("stopCmd" to Command.STOP.parseWithPrefix(context.prefix)),
	logMessage = "Attempt to invoke radio command, while non-continuous audio stream is active.",
)
