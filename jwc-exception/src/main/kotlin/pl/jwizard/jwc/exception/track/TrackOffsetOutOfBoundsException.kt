/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.track

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when an offset is attempted outside the valid range in a track queue.
 *
 * @param commandBaseContext The context of the command that triggered this exception.
 * @param offset The attempted offset value.
 * @param maxOffset The maximum allowable offset value.
 * @author Miłosz Gilga
 */
class TrackOffsetOutOfBoundsException(
	commandBaseContext: CommandBaseContext,
	offset: Int,
	maxOffset: Int,
) : CommandPipelineExceptionHandler(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.TRACK_OFFSET_OUT_OF_BOUNDS,
	variables = mapOf("maxOffset" to maxOffset),
	logMessage = """
		Attempt to offset to out of bounds track position in queue.
		Offset: "$offset". Max offset: "$maxOffset".
	""",
)
