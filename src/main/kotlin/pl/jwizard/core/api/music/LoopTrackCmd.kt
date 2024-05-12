/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter

@CommandListenerBean(id = BotCommand.LOOP)
class LoopTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		inPlayingMode = true
		onSameChannelWithBot = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		val isRepeating = playerManagerFacade.toggleInfiniteLoopTrack(event)
		val playingTrack = playerManagerFacade.currentPlayingTrack(event)

		val messagePlaceholder = if (isRepeating) {
			I18nResLocale.ADD_TRACK_TO_INFINITE_LOOP
		} else {
			I18nResLocale.REMOVED_TRACK_FROM_INFINITE_LOOP
		}
		val embedMessage = CustomEmbedBuilder(event, botConfiguration)
			.addAuthor()
			.addDescription(
				placeholder = messagePlaceholder,
				params = mapOf(
					"track" to Formatter.createRichTrackTitle(playingTrack as AudioTrackInfo),
					"loopCmd" to BotCommand.LOOP.parseWithPrefix(botConfiguration, event),
				)
			)
			.addThumbnail(playingTrack.artworkUrl)
			.addColor(EmbedColor.WHITE)
			.build()
		event.appendEmbedMessage(embedMessage)
	}
}
