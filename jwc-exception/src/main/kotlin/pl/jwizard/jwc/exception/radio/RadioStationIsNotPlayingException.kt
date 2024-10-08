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
 * Exception thrown when a command is invoked but there is no radio station currently playing.
 *
 * @param context The context of the command that caused the exception.
 * @param command The radio command prefix for retrying the operation if necessary.
 * @author Miłosz Gilga
 */
class RadioStationIsNotPlayingException(
	context: CommandBaseContext,
	command: CommandPrefix,
) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.RADIO_STATION_IS_NOT_PLAYING,
	variables = mapOf("playRadioStationCmd" to command.parseWithPrefix(context)),
	logMessage = "Attempt to invoke command, while radio station is not playing.",
)
