/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.track

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when the number of track repeats exceeds allowed limits.
 *
 * @param context The context of the command that triggered this exception.
 * @param maxRepeats The maximum number of repeats that was attempted to set.
 * @author Miłosz Gilga
 */
class TrackRepeatsOutOfBoundException(
	context: CommandBaseContext,
	maxRepeats: Int,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.TRACK_REPEATS_OUT_OF_BOUNDS,
	args = mapOf("maxRepeats" to maxRepeats),
	logMessage = "Attempt to set out of bounds current audio track repeats number: \"$maxRepeats\".",
)
