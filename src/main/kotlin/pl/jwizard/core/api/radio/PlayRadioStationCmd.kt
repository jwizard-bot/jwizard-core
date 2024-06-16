/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.radio

import pl.jwizard.core.api.AbstractRadioCmd
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.RadioException

@CommandListenerBean(id = BotCommand.PLAY_RADIO)
class PlayRadioStationCmd(
	playerManager: PlayerManager,
	botConfiguration: BotConfiguration,
) : AbstractRadioCmd(
	playerManager,
	botConfiguration,
) {
	init {
		selfJoinable = true
		onSameChannelWithBot = true
		isRadioShouldIdle = true
	}

	override fun executeRadioCmd(event: CompoundCommandEvent, openAudioConnection: Boolean) {
		val radioStationSlug = getArg<String>(CommandArgument.RADIO_STATION, event)

		// check, if radio channel is disabled on selected guild
		val radioStation = radioSupplier.fetchRadioStation(radioStationSlug, event.guildDbId)
			?: throw RadioException.RadioStationNotExistsOrTurnedOffException(event, radioStationSlug)

		if (openAudioConnection) {
			joinToVoiceAndOpenAudioConnection(event) // join to channel after check radio station
		}
		// load radio station details stream
		playerManager.loadAndStream(event, radioStation)
	}
}
