package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class RadioStationNotExistsOrTurnedOffException(
	context: CommandBaseContext,
	radioStationSlug: String,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.RADIO_STATION_NOT_EXISTS_IS_TURNED_OFF,
	logMessage = """
		Attempt to invoke command, while radio station: "$radioStationSlug" is turned off.
	""",
)
