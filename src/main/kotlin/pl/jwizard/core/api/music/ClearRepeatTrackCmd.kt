/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter

@CommandListenerBean(id = BotCommand.REPEATCLS)
class ClearRepeatTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManager
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		playerManager.setTrackRepeat(event, 0)
		val currentPlayingTrack = playerManager.currentPlayingTrack(event)

		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(
				placeholder = I18nResLocale.REMOVE_MULTIPLE_REPEATING_TRACK,
				params = mapOf(
					"track" to Formatter.createRichTrackTitle(currentPlayingTrack as AudioTrackInfo),
					"repeatingCmd" to BotCommand.REPEAT.parseWithPrefix(event),
				)
			)
			.addThumbnail(currentPlayingTrack.artworkUrl)
			.addColor(EmbedColor.WHITE)
			.build()
		event.appendEmbedMessage(embedMessage)
	}
}
