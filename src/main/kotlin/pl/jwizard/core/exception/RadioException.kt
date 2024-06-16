/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.exception

import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent

object RadioException {
	class RadioStationNotExistsOrTurnedOffException(
		event: CompoundCommandEvent,
		stationSlug: String
	) : AbstractBotException(
		event, RadioStationNotExistsOrTurnedOffException::class,
		i18nLocale = I18nExceptionLocale.RADIO_STATION_NOT_EXISTS_IS_TURNED_OFF,
		logMessage = "Attempt to invoke command, while radio station: $stationSlug is turned off"
	)

	class RadioStationIsNotPlayingException(event: CompoundCommandEvent) : AbstractBotException(
		event, RadioStationIsNotPlayingException::class,
		i18nLocale = I18nExceptionLocale.RADIO_STATION_IS_NOT_PLAYING,
		variables = mapOf("playRadioStationCmd" to BotCommand.PLAY_RADIO.parseWithPrefix(event)),
		logMessage = "Attempt to invoke command, while radio station is not playing"
	)

	class RadioStationIsPlayingException(event: CompoundCommandEvent) : AbstractBotException(
		event, RadioStationIsPlayingException::class,
		i18nLocale = I18nExceptionLocale.RADIO_STATION_IS_PLAYING,
		variables = mapOf("stopRadioStationCmd" to BotCommand.STOP_RADIO.parseWithPrefix(event)),
		logMessage = "Attempt to invoke command, while radio station is currently playing"
	)

	class DiscreteAudioStreamIsPlayingException(event: CompoundCommandEvent) : AbstractBotException(
		event, DiscreteAudioStreamIsPlayingException::class,
		i18nLocale = I18nExceptionLocale.DISCRETE_AUDIO_STREAM_IS_PLAYING,
		variables = mapOf("stopCmd" to BotCommand.STOP.parseWithPrefix(event)),
		logMessage = "Attempt to invoke radio command, while non-continuous audio stream is active"
	)

	class RadioStationNotProvidedPlaybackDataException(event: CompoundCommandEvent) : AbstractBotException(
		event, RadioStationNotProvidedPlaybackDataException::class,
		i18nLocale = I18nExceptionLocale.RADIO_STATION_NOT_PROVIDING_PLAYBACK_DATA,
		logMessage = "Attempt to invoke radio command, while radio not providing any information about audio stream playback"
	)
}
