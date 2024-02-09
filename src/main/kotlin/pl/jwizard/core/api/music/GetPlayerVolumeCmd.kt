/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale

@CommandListenerBean(id = BotCommand.GETVOLUME)
class GetPlayerVolumeCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		inIdleMode = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		val embedMessage = CustomEmbedBuilder(event, botConfiguration).buildBaseMessage(
			placeholder = I18nResLocale.GET_CURRENT_AUDIO_PLAYER_VOLUME,
			params = mapOf(
				"currentVolume" to playerManagerFacade.findMusicManager(event).audioPlayer.volume,
			),
		)
		event.appendEmbedMessage(embedMessage)
	}
}
