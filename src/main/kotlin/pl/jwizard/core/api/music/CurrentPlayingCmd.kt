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
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nMiscLocale

@CommandListenerBean(id = BotCommand.PLAYING)
class CurrentPlayingCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		inPlayingMode = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		val musicManager = playerManagerFacade.findMusicManager(event)
		val playingTrackInfo = playerManagerFacade.currentPlayingTrack(event)
			?: throw AudioPlayerException.TrackIsNotPlayingException(event)

		val messageEmbed = createDetailedTrackEmbedMessage(
			event,
			i18nDescription = I18nMiscLocale.CURRENT_PLAYING_TRACK,
			i18nTimestampText = I18nMiscLocale.CURRENT_PLAYING_TIMESTAMP,
			track = playingTrackInfo,
			musicManager
		)
		event.appendEmbedMessage(messageEmbed)
	}
}
