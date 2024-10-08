/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when an attempt is made to set the audio player's volume outside the allowable bounds.
 *
 * @param context The context of the command that caused the exception.
 * @param volume The volume level that was attempted to be set.
 * @param minVolume The minimum allowable volume level.
 * @param maxVolume The maximum allowable volume level.
 * @author Miłosz Gilga
 */
class VolumeUnitsOutOfBoundsException(
	context: CommandBaseContext,
	volume: Int,
	minVolume: Int,
	maxVolume: Int,
) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.VOLUME_UNITS_OUT_OF_BOUNDS,
	variables = mapOf(
		"minVolume" to minVolume,
		"maxVolume" to maxVolume,
	),
	logMessage = "Attempt to set out of bounds ($minVolume - $maxVolume, value: $volume) audio player volume units.",
)
