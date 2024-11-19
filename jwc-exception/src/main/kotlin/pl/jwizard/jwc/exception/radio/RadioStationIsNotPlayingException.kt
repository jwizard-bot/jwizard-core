/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a command is invoked but there is no radio station currently playing.
 *
 * @param context The context of the command that caused the exception.
 * @author Miłosz Gilga
 */
class RadioStationIsNotPlayingException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.RADIO_STATION_IS_NOT_PLAYING,
	args = mapOf("playRadioStationCmd" to Command.PLAYRADIO.parseWithPrefix(context.prefix)),
	logMessage = "Attempt to invoke command, while radio station is not playing.",
)
