/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.action

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component
import pl.jwizard.core.api.AbstractMusicCmd.Companion.createDetailedTrackEmbedMessage
import pl.jwizard.core.api.radio.RadioInfoCmd
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.db.GuildSettingsSupplier
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.radioplayback.RadioPlaybackClassLoader

@Component
class ActionCommandListener(
	private val botConfiguration: BotConfiguration,
	private val playerManager: PlayerManager,
	private val guildSettingsSupplier: GuildSettingsSupplier,
	private val radioPlaybackClassLoader: RadioPlaybackClassLoader,
) : AbstractLoggingBean(ActionCommandListener::class), ActionProxyHandler {

	override fun updateCurrentPlayingEmbedMessage(buttonClickEvent: ButtonInteractionEvent) {
		val existingEmbedMessage = checkIfEmbedIsChanged(buttonClickEvent) ?: return

		val guild = buttonClickEvent.guild!!
		val musicManager = playerManager.findMusicManager(guild.id)
		val currentPlayingTrack = musicManager?.audioPlayer?.playingTrack
		if (currentPlayingTrack == null) {
			responseWithEmbed(buttonClickEvent, existingEmbedMessage)
			return
		}
		val playingTrack = ExtendedAudioTrackInfo(currentPlayingTrack)
		val guildLang = guildSettingsSupplier.fetchGuildLang(guild.id)
		val embedMessage = createDetailedTrackEmbedMessage(
			botConfiguration,
			guildLang,
			i18nDescription = I18nMiscLocale.CURRENT_PLAYING_TRACK,
			i18nTimestampText = I18nMiscLocale.CURRENT_PLAYING_TIMESTAMP,
			track = playingTrack,
			author = playingTrack.sender,
		)
		responseWithEmbed(buttonClickEvent, embedMessage)
	}

	override fun updateRadioPlaybackEmbedMessage(buttonClickEvent: ButtonInteractionEvent) {
		val existingEmbedMessage = checkIfEmbedIsChanged(buttonClickEvent) ?: return
		val guild = buttonClickEvent.guild!!
		val musicManager = playerManager.findMusicManager(guild.id)
		val radioStation = musicManager?.actions?.radioStationDto
		// update only, if player is in streaming mode and radio is playing
		val audioStream = musicManager?.audioPlayer?.playingTrack
		if (radioStation == null || audioStream == null) {
			responseWithEmbed(buttonClickEvent, existingEmbedMessage)
			return
		}
		val dataFetcher = radioStation.slug.let { radioPlaybackClassLoader.loadClass(it) }
		// update only for radio station which provide real-time playback info
		if (dataFetcher == null) {
			responseWithEmbed(buttonClickEvent, existingEmbedMessage)
			return
		}
		// fetch data from selected data fetcher bean
		val playbackData = dataFetcher.fetchData(radioStation.slug)
		if (playbackData == null) {
			responseWithEmbed(buttonClickEvent, existingEmbedMessage)
			return
		}
		val guildLang = guildSettingsSupplier.fetchGuildLang(guild.id)
		val audioStreamSender = ExtendedAudioTrackInfo(audioStream)
		responseWithEmbed(
			buttonClickEvent,
			RadioInfoCmd.createEmbedMessage(botConfiguration, guildLang, radioStation, playbackData, audioStreamSender.sender)
		)
	}

	private fun checkIfEmbedIsChanged(buttonClickEvent: ButtonInteractionEvent): MessageEmbed? {
		val embedMessage = buttonClickEvent.message.embeds[0]
		if (embedMessage.isEmpty || buttonClickEvent.guild == null) {
			return null
		}
		return embedMessage
	}

	private fun responseWithEmbed(
		buttonClickEvent: ButtonInteractionEvent,
		existingEmbedMessage: MessageEmbed,
	) = buttonClickEvent.editMessageEmbeds(existingEmbedMessage).queue()
}
