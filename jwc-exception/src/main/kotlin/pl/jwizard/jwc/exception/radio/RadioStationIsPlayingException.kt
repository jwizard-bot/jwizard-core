/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.CommandPrefix
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when a command is invoked while a radio station is already playing.
 *
 * @param commandBaseContext The context of the command that caused the exception.
 * @param command The radio command prefix for stopping the current station.
 * @author Miłosz Gilga
 */
class RadioStationIsPlayingException(
	commandBaseContext: CommandBaseContext,
	command: CommandPrefix,
) : CommandPipelineExceptionHandler(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.RADIO_STATION_IS_PLAYING,
	variables = mapOf("stopRadioStationCmd" to command.parseWithPrefix(commandBaseContext)),
	logMessage = "Attempt to invoke command, while radio station is currently playing."
)
