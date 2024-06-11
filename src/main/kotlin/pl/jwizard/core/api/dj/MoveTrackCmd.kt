/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.dj

import pl.jwizard.core.api.AbstractDjCmd
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.TrackPosition
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter

@CommandListenerBean(id = BotCommand.MOVE)
class MoveTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractDjCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
	}

	override fun executeDjCmd(event: CompoundCommandEvent) {
		val trackPosition = TrackPosition(
			previous = getArg(CommandArgument.FROM_POS, event),
			selected = getArg(CommandArgument.TO_POS, event),
		)
		val movedTrack = playerManagerFacade.moveTrackToPos(event, trackPosition)
		val (previous, selected) = trackPosition

		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(
				placeholder = I18nResLocale.MOVE_TRACK_POS_TO_SELECTED_LOCATION,
				params = mapOf(
					"movedTrack" to Formatter.createRichTrackTitle(movedTrack.info),
					"previousPosition" to previous,
					"requestedPosition" to selected,
				)
			)
			.addColor(EmbedColor.WHITE)
			.addThumbnail(ExtendedAudioTrackInfo(movedTrack).artworkUrl)
			.build()
		event.appendEmbedMessage(embedMessage)
	}
}
