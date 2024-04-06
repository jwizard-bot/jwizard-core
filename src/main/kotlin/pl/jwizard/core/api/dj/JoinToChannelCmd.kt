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

@CommandListenerBean(id = BotCommand.JOIN)
class JoinToChannelCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractDjCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		inPlayingMode = true
		allowAlsoForNormal = false
	}

	override fun executeDjCmd(event: CompoundCommandEvent) {
		val movedToVoiceChannel = playerManagerFacade.moveToMemberCurrentVoiceChannel(event)
		val embedMessage = CustomEmbedBuilder(event, botConfiguration)
			.addAuthor()
			.addDescription(
				placeholder = I18nResLocale.MOVE_BOT_TO_SELECTED_CHANNEL,
				params = mapOf(
					"movedChannel" to movedToVoiceChannel.name,
				)
			)
			.addColor(EmbedColor.WHITE)
			.build()
		event.appendEmbedMessage(embedMessage)
	}
}
