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
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale

@CommandListenerBean(id = BotCommand.INFINITE)
class InfinitePlaylistCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractDjCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
		inPlayingMode = true
	}

	override fun executeDjCmd(event: CompoundCommandEvent) {
		val isInfiniteRepeating = playerManagerFacade.toggleInfiniteLoopPlaylist(event)
		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(
				placeholder = if (isInfiniteRepeating) {
					I18nResLocale.ADD_PLAYLIST_TO_INFINITE_LOOP
				} else {
					I18nResLocale.REMOVED_PLAYLIST_FROM_INFINITE_LOOP
				},
				params = mapOf(
					"playlistLoopCmd" to BotCommand.INFINITE.parseWithPrefix(event),
				)
			)
			.addColor(EmbedColor.WHITE)
			.build()
		event.appendEmbedMessage(embedMessage)
	}
}
