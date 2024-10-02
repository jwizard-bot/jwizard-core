/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when a command is invoked while the bot is not playing any audio content.
 *
 * @param commandBaseContext The context of the command that triggered this exception.
 * @author Miłosz Gilga
 */
class ActiveAudioPlayingNotFoundException(
	commandBaseContext: CommandBaseContext,
) : CommandPipelineExceptionHandler(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.ACTIVE_AUDIO_PLAYING_NOT_FOUND,
	logMessage = "Attempt to invoke command while bot is not playing any audio content.",
)
