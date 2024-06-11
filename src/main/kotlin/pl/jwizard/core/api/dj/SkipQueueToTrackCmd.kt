/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.dj

import pl.jwizard.core.api.AbstractDjCmd
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter

@CommandListenerBean(id = BotCommand.SKIPTO)
class SkipQueueToTrackCmd(
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
		val trackPosition = getArg<Int>(CommandArgument.POS, event)

		val musicManager = playerManagerFacade.findMusicManager(event)
		if (musicManager.actions.checkInvertedTrackPosition(trackPosition)) {
			throw AudioPlayerException.TrackPositionOutOfBoundsException(event, musicManager.queue.size)
		}
		val currentPlayingTrack = playerManagerFacade.skipToTrackPos(event, trackPosition)
		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(
				placeholder = I18nResLocale.SKIP_TO_SELECT_TRACK_POSITION,
				params = mapOf(
					"countOfSkippedTracks" to trackPosition - 1,
					"currentTrack" to Formatter.createRichTrackTitle(currentPlayingTrack.info),
				)
			)
			.addColor(EmbedColor.WHITE)
			.addThumbnail(ExtendedAudioTrackInfo(currentPlayingTrack).artworkUrl)
			.build()
		event.appendEmbedMessage(embedMessage)
	}
}
