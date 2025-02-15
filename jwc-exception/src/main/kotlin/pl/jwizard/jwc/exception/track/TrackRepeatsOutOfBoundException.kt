package pl.jwizard.jwc.exception.track

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class TrackRepeatsOutOfBoundException(
	context: CommandBaseContext,
	minRepeats: Int,
	maxRepeats: Int,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.TRACK_REPEATS_OUT_OF_BOUNDS,
	args = mapOf(
		"minRepeats" to minRepeats,
		"maxRepeats" to maxRepeats,
	),
	logMessage = """
		Attempt to set out of bounds current audio track repeats number: "$minRepeats" (min) and
		"$maxRepeats" (max).
	""",
)
