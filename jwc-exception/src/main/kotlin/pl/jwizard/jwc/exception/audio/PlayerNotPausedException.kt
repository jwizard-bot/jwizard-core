/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a command is invoked while the audio player is not paused.
 *
 * @param context The context of the command that triggered this exception.
 * @author Miłosz Gilga
 */
class PlayerNotPausedException(context: CommandBaseContext) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.PLAYER_NOT_PAUSED,
	logMessage = "Attempt to invoke command while audio player is not paused.",
)
