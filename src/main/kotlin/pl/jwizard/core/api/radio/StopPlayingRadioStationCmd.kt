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
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = BotCommand.STOP_RADIO)
class StopPlayingRadioStationCmd(
	playerManager: PlayerManager,
	botConfiguration: BotConfiguration,
) : AbstractRadioCmd(
	playerManager,
	botConfiguration,
) {
	init {
		onSameChannelWithBot = true
		isRadioShouldPlaying = true
	}

	override fun executeRadioCmd(event: CompoundCommandEvent, openAudioConnection: Boolean) {
		val musicManager = playerManager.findMusicManager(event)

		// stopping radio stream and remove guild from map
		musicManager.audioScheduler.setCompoundEvent(event)
		musicManager.actions.clearAndDestroy(false)
	}
}
