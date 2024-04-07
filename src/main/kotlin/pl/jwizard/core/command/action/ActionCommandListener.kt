/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.action

import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent

@Component
class ActionCommandListener(
	private val botConfiguration: BotConfiguration,
	private val playerManagerFacade: PlayerManagerFacade
) : AbstractLoggingBean(ActionCommandListener::class), ActionProxyHandler {

	override fun updateCurrentPlayingEmbedMessage(buttonClickEvent: ButtonClickEvent) {
		var embedMessage = buttonClickEvent.message.embeds[0]
		if (embedMessage.isEmpty || buttonClickEvent.guild == null) {
			return
		}
		val musicManager = playerManagerFacade.findMusicManager(buttonClickEvent.guild!!)
		val currentPlayingTrack = musicManager?.audioPlayer?.playingTrack
		if (currentPlayingTrack != null) {
			val playingTrack = ExtendedAudioTrackInfo(currentPlayingTrack)
			embedMessage = CustomEmbedBuilder(botConfiguration, buttonClickEvent.guild!!.id)
				.addAuthor(playingTrack.sender)
				.addDescription(I18nMiscLocale.CURRENT_PLAYING_TRACK)
				.appendKeyValueField(I18nMiscLocale.TRACK_NAME, Formatter.createRichTrackTitle(playingTrack))
				.addSpace()
				.appendKeyValueField(I18nMiscLocale.TRACK_ADDDED_BY, playingTrack.sender.asTag)
				.appendValueField(Formatter.createPercentageRepresentation(playingTrack), false)
				.appendKeyValueField(
					I18nMiscLocale.CURRENT_PLAYING_TIMESTAMP,
					Formatter.createTrackCurrentAndMaxDuration(playingTrack)
				)
				.addSpace()
				.appendKeyValueField(
					I18nMiscLocale.CURRENT_TRACK_LEFT_TO_NEXT,
					DateUtils.convertMilisToDTF(playingTrack.approximateTime)
				)
				.addThumbnail(playingTrack.thumbnailUrl)
				.addColor(EmbedColor.WHITE)
				.build()
		}
		buttonClickEvent.editMessageEmbeds(embedMessage).queue()
	}
}
