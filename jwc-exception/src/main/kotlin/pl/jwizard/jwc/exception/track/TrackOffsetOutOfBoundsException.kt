package pl.jwizard.jwc.exception.track

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class TrackOffsetOutOfBoundsException(
	context: CommandBaseContext,
	offset: Int,
	maxOffset: Int,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.TRACK_OFFSET_OUT_OF_BOUNDS,
	args = mapOf("maxOffset" to maxOffset),
	logMessage = """
		Attempt to offset to out of bounds track position in queue. Offset: "$offset". Max offset:
		"$maxOffset".
	""",
)
