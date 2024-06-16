/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.radio

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.context.ApplicationContext
import pl.jwizard.core.api.AbstractRadioCmd
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.action.ActionComponent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.db.RadioStationDto
import pl.jwizard.core.exception.RadioException
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.radioplayback.RadioPlaybackResponseData
import pl.jwizard.core.radioplayback.RadioStationPlayback

@CommandListenerBean(id = BotCommand.RADIO_INFO)
class RadioInfoCmd(
	private val applicationContext: ApplicationContext,
	playerManager: PlayerManager,
	botConfiguration: BotConfiguration,
) : AbstractRadioCmd(
	playerManager,
	botConfiguration,
) {
	init {
		onSameChannelWithBot = true
		isRadioShouldPlaying = true
	}

	override fun executeRadioCmd(event: CompoundCommandEvent, openAudioConnection: Boolean) {
		val musicManager = playerManager.findMusicManager(event)

		val radioStation = musicManager.actions.radioStationDto
		val dataFetcher = radioStation?.slug?.let { RadioStationPlayback.getBeanBaseSlug(applicationContext, it) }
			?: throw RadioException.RadioStationNotProvidedPlaybackDataException(event)

		// fetch data from selected data fetcher bean
		val playbackData = dataFetcher.fetchData(radioStation.slug)
			?: throw RadioException.RadioStationNotProvidedPlaybackDataException(event)

		val embedMessage = createEmbedMessage(
			botConfiguration, event.lang, radioStation, playbackData,
			event.dataSender?.user
		)
		val button = createButton(
			actionComponent = ActionComponent.UPDATE_RADIO_PLAYBACK_EMBED_MESSAGE,
			style = ButtonStyle.SECONDARY,
			placeholder = I18nMiscLocale.REFRESH_BUTTON,
			lang = event.lang,
		)
		event.appendEmbedMessage(embedMessage)
		event.addWebhookActionComponents(button)
	}

	companion object {
		fun createEmbedMessage(
			botConfiguration: BotConfiguration,
			lang: String,
			radioStation: RadioStationDto,
			data: RadioPlaybackResponseData,
			sender: User?,
		): MessageEmbed {
			val embedBuilder = CustomEmbedBuilder(botConfiguration, lang)
				.addAuthor(sender?.name, sender?.avatarUrl ?: sender?.defaultAvatarUrl)
				.addDescription(
					I18nResLocale.CURRENTLY_PLAYING_STREAM_CONTENT,
					mapOf("radioStationName" to radioStation.name),
				)
				.appendKeyValueField(I18nMiscLocale.TRACK_NAME, data.title)
			data.nextPlay?.let {
				embedBuilder.addSpace()
				embedBuilder.appendKeyValueField(I18nMiscLocale.NEXT_TRACK_INDEX_MESS, it)
			}
			data.percentageBar?.let { embedBuilder.appendValueField(it, false) }
			data.trackDuration?.let {
				embedBuilder.appendKeyValueField(I18nMiscLocale.TRACK_DURATION_TIME, it)
			}
			data.toNextPlayDuration?.let {
				embedBuilder.addSpace()
				embedBuilder.appendKeyValueField(I18nMiscLocale.APPROX_TO_NEXT_TRACK_FROM_QUEUE, it)
			}
			val footerContent = "%s: %s".format(
				botConfiguration.i18nService.getMessage(I18nMiscLocale.DATA_COMES_FROM, lang),
				data.providedBy,
			)
			embedBuilder.addThumbnail(data.streamThumbnailUrl)
			embedBuilder.addFooter(footerContent)
			embedBuilder.addColor(EmbedColor.WHITE)
			return embedBuilder.build()
		}
	}
}
