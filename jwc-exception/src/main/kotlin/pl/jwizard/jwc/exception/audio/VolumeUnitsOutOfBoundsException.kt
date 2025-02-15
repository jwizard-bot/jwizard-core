package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class VolumeUnitsOutOfBoundsException(
	context: CommandBaseContext,
	volume: Int,
	minVolume: Int,
	maxVolume: Int,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.VOLUME_UNITS_OUT_OF_BOUNDS,
	args = mapOf(
		"minVolume" to minVolume,
		"maxVolume" to maxVolume,
	),
	logMessage = """
		Attempt to set out of bounds ($minVolume - $maxVolume, value: $volume) audio player volume
		units.
	""",
)
