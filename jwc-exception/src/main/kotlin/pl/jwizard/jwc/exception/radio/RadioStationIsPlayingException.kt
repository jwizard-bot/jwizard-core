package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class RadioStationIsPlayingException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.RADIO_STATION_IS_PLAYING,
	args = mapOf("stopRadioStationCmd" to Command.RADIO_STOP.parseWithPrefix(context)),
	logMessage = "Attempt to invoke command, while radio station is currently playing.",
)
