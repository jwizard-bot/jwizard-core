/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.track

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when an attempt is made to move a track to the same position.
 *
 * @param context The context of the command that triggered this exception.
 * @author Miłosz Gilga
 */
class TrackPositionsIsTheSameException(context: CommandBaseContext) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.TRACK_THE_SAME_POSITION,
	logMessage = "Attempt to move track to the same origin position.",
)