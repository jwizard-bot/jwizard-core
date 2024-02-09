/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.dj

import pl.jwizard.core.api.AbstractDjCmd
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.CommandException
import pl.jwizard.core.i18n.I18nResLocale

@CommandListenerBean(id = BotCommand.SETVOLUME)
class SetPlayerVolumeCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractDjCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		inIdleMode = true
	}

	override fun executeDjCmd(event: CompoundCommandEvent) {
		val newVolumeUnits = getArg<Int>(CommandArgument.VOLUME, event)

		val currentVolume = playerManagerFacade.findMusicManager(event).currentPlayerVolume
		if (currentVolume < 0 || currentVolume > 150) {
			throw CommandException.VolumeUnitsOutOfBoundsException(event, 0, 150)
		}
		playerManagerFacade.setPlayerVolume(event, newVolumeUnits)
		val embedMessage = CustomEmbedBuilder(event, botConfiguration).buildBaseMessage(
			placeholder = I18nResLocale.SET_CURRENT_AUDIO_PLAYER_VOLUME,
			params = mapOf(
				"previousVolume" to currentVolume,
				"setVolume" to newVolumeUnits,
			),
		)
		event.appendEmbedMessage(embedMessage)
	}
}
