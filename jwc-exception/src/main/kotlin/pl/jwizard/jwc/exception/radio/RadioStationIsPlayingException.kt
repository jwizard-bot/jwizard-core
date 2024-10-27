/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.CommandPrefix
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a command is invoked while a radio station is already playing.
 *
 * @param context The context of the command that caused the exception.
 * @param command The radio command prefix for stopping the current station.
 * @author Miłosz Gilga
 */
class RadioStationIsPlayingException(
	context: CommandBaseContext,
	command: CommandPrefix,
) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.RADIO_STATION_IS_PLAYING,
	args = mapOf("stopRadioStationCmd" to command.parseWithPrefix(context)),
	logMessage = "Attempt to invoke command, while radio station is currently playing.",
)
