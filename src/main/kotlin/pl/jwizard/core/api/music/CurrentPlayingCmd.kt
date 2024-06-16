/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.action.ActionComponent
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nMiscLocale

@CommandListenerBean(id = BotCommand.PLAYING)
class CurrentPlayingCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManager
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		inPlayingMode = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		val playingTrackInfo = playerManager.currentPlayingTrack(event)
			?: throw AudioPlayerException.TrackIsNotPlayingException(event)

		val messageEmbed = createDetailedTrackEmbedMessage(
			botConfiguration,
			event.lang,
			i18nDescription = I18nMiscLocale.CURRENT_PLAYING_TRACK,
			i18nTimestampText = I18nMiscLocale.CURRENT_PLAYING_TIMESTAMP,
			track = playingTrackInfo,
			author = playingTrackInfo.sender,
		)
		val button = createButton(
			actionComponent = ActionComponent.UPDATE_CURRENT_PLAYING_EMBED_MESSAGE,
			style = ButtonStyle.SECONDARY,
			placeholder = I18nMiscLocale.REFRESH_BUTTON,
			lang = event.lang,
		)
		event.appendEmbedMessage(messageEmbed)
		event.addWebhookActionComponents(button)
	}
}
