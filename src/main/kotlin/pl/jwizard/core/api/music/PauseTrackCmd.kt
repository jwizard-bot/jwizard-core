/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter

@CommandListenerBean(id = BotCommand.PAUSE)
class PauseTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManager
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		inPlayingMode = true
		onSameChannelWithBot = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		playerManager.pauseTrack(event)
		val pausedTrackInfo = playerManager.currentPlayingTrack(event)
			?: throw AudioPlayerException.TrackIsNotPlayingException(event)

		val embedMessage = createCurrentPausedTrackMessage(
			event,
			track = pausedTrackInfo
		)
		event.appendEmbedMessage(embedMessage)
	}

	private fun createCurrentPausedTrackMessage(
		event: CompoundCommandEvent,
		track: ExtendedAudioTrackInfo,
	) = CustomEmbedBuilder(botConfiguration, event)
		.addAuthor()
		.addDescription(
			placeholder = I18nResLocale.PAUSED_TRACK,
			params = mapOf(
				"track" to Formatter.createRichTrackTitle(track),
				"resumeCmd" to BotCommand.RESUME.parseWithPrefix(event),
			),
		)
		.appendValueField(Formatter.createPercentageRepresentation(track), false)
		.appendKeyValueField(I18nMiscLocale.PAUSED_TRACK_TIME, DateUtils.convertMilisToDTF(track.timestamp))
		.appendKeyValueField(
			I18nMiscLocale.PAUSED_TRACK_ESTIMATE_TIME,
			DateUtils.convertMilisToDTF(track.approximateTime)
		)
		.appendKeyValueField(
			I18nMiscLocale.PAUSED_TRACK_TOTAL_DURATION,
			DateUtils.convertMilisToDTF(track.maxDuration)
		)
		.addColor(EmbedColor.WHITE)
		.addThumbnail(track.artworkUrl)
		.build()
}
