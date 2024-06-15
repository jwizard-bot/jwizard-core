/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nMiscLocale

@CommandListenerBean(id = BotCommand.PAUSED)
class CurrentPausedCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManager
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		isPaused = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		val musicManager = playerManager.findMusicManager(event)
		val pausedTrackInfo = musicManager.actions.getPausedTrackInfo()

		val messageEmbed = createDetailedTrackEmbedMessage(
			event,
			i18nDescription = I18nMiscLocale.CURRENT_PAUSED_TRACK,
			i18nTimestampText = I18nMiscLocale.CURRENT_PAUSED_TIMESTAMP,
			track = pausedTrackInfo
		)
		event.appendEmbedMessage(messageEmbed)
	}
}
