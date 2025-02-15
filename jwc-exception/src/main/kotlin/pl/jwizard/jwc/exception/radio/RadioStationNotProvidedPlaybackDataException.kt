package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.radio.RadioStation

class RadioStationNotProvidedPlaybackDataException(
	context: CommandBaseContext,
	radioStation: RadioStation,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.RADIO_STATION_NOT_PROVIDING_PLAYBACK_DATA,
	logMessage = """
		Attempt to invoke radio command, while radio: "${radioStation.textKey}" not providing any
		information about audio stream playback.
	""",
)
