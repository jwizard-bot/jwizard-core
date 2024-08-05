/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.scheduler

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import org.apache.commons.lang3.StringUtils
import pl.jwizard.core.cdn.CdnResource
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.db.RadioStationDto
import pl.jwizard.core.exception.I18nExceptionLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.log.AbstractLoggingBean

class StreamSchedulerFacade(
	private val audioScheduler: AudioScheduler,
) : AbstractLoggingBean(StreamSchedulerFacade::class), AudioSchedulerContract {

	private val actions = audioScheduler.schedulerActions
	private val botConfiguration = audioScheduler.botConfiguration

	override fun onStart() {
		actions.threadsCountToLeave?.cancel(false) // stopping thread counting down to leave after inactivity

		val event = audioScheduler.event
		val station = actions.radioStationDto ?: return // skipping on non-radio station event

		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(
				placeholder = I18nResLocale.START_PLAYING_RADIO_STATION,
				params = mapOf("radioStationName" to station.name),
			)
			.appendDescriptionList(
				elements = mapOf(
					I18nResLocale.START_PLAYING_RADIO_STATION_FIRST_OPTION to mapOf(
						"stopRadioStationCmd" to BotCommand.STOP_RADIO.parseWithPrefix(event)
					),
					I18nResLocale.START_PLAYING_RADIO_STATION_SECOND_OPTION to mapOf(
						"radioStationInfoCmd" to BotCommand.RADIO_INFO.parseWithPrefix(event)
					)
				)
			)
			.addColor(EmbedColor.WHITE)
			.addThumbnail(getRadioStationThumbnail(station))
			.build()

		jdaLog.info(
			event, "Radio station: ${station.name} from server ${station.streamUrl} (proxy: ${station.proxyStreamUrl}) " +
				"has started"
		)
		event.instantlySendEmbedMessage(embedMessage)
	}

	override fun onEnd(track: AudioTrack, endReason: AudioTrackEndReason) {
		val event = audioScheduler.event
		val station = actions.radioStationDto ?: return // skipping on non-conitinuous (radio) event

		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(
				placeholder = I18nResLocale.STOP_PLAYING_RADIO_STATION,
				params = mapOf(
					"radioStationName" to station.name,
					"startRadioStationCmd" to BotCommand.PLAY_RADIO.parseWithPrefix(event)
				)
			)
			.addColor(EmbedColor.WHITE)
			.addThumbnail(getRadioStationThumbnail(station))
			.build()

		actions.radioStationDto = null
		actions.leaveAndSendMessageAfterInactivity()

		jdaLog.info(
			event, "Radio station: ${station.name} from server ${station.streamUrl} (proxy: ${station.proxyStreamUrl}) " +
				"has ended streaming. Return to regular queue"
		)
		event.instantlySendEmbedMessage(embedMessage)
	}

	override fun onException(track: AudioTrack, ex: FriendlyException) {
		val event = audioScheduler.event
		val station = actions.radioStationDto ?: return // skipping on non-conitinuous (radio) event

		val messageEmbed = CustomEmbedBuilder(botConfiguration, event).buildErrorMessage(
			placeholder = I18nExceptionLocale.UNXEPECTED_ERROR_WHILE_STREAMING_RADIO,
			params = mapOf("radioStation" to station.name)
		)
		event.instantlySendEmbedMessage(messageEmbed)
		jdaLog.error(event, "Unexpected error while streaming radio stream ${station.name}. Cause: ${ex.message}")
	}

	private fun getRadioStationThumbnail(radioStation: RadioStationDto): String {
		val coverImage = radioStation.coverImage
		return if (coverImage != null) {
			CdnResource.RADIO_STATIONS.getResourceUrl(botConfiguration, coverImage)
		} else {
			StringUtils.EMPTY
		}
	}
}
