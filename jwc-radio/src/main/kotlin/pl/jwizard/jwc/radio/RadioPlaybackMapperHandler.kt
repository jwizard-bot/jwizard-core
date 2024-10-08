/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio

import com.fasterxml.jackson.databind.JsonNode
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nDynamicMod
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.jda.embed.PercentageIndicatorBar
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.radio.RadioStationDetails
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMessage
import pl.jwizard.jwc.core.util.dtFormat
import pl.jwizard.jwc.core.util.ext.getAsText
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.exception.radio.RadioStationNotProvidedPlaybackDataException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

/**
 * Abstract class for handling the mapping of radio playback data. This class provides methods to create playback data
 * messages and parse responses from external audio service providers.
 *
 * @property environment The environment containing necessary beans for playback mapping.
 * @author Miłosz Gilga
 */
abstract class RadioPlaybackMapperHandler(
	private val environment: RadioPlaybackMapperEnvironment,
) : RadioPlaybackMessage {

	private val i18nBean = environment.i18nBean
	private val jdaColorStoreBean = environment.jdaColorStoreBean
	protected val objectMapper = environment.objectMapper

	/**
	 * The query for redirecting to extended link, retrieved from the environment properties.
	 */
	private val redirectQuery = environment.environmentBean.getProperty<String>(BotProperty.RADIO_PLAYBACK_EXTENDED_LINK)

	/**
	 * The HTTP client used for making requests to playback APIs.
	 */
	private val httpClient = HttpClient.newHttpClient()

	/**
	 * Creates a playback data message for the specified radio station details.
	 *
	 * @param details The details of the radio station.
	 * @param context The command context containing guild-specific information.
	 * @return A MessageEmbed containing the playback data for the radio station.
	 * @throws RadioStationNotProvidedPlaybackDataException if the playback data cannot be retrieved.
	 */
	override fun createPlaybackDataMessage(details: RadioStationDetails, context: CommandBaseContext): MessageEmbed {
		if (details.playbackApiUrl == null) {
			throw RadioStationNotProvidedPlaybackDataException(context, details.name)
		}
		val requestUri = URI.create(details.playbackApiUrl!!)
		val httpRequest = HttpRequest.newBuilder()
			.uri(requestUri)
			.build()

		val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
		if (response.statusCode() != 200) {
			throw RadioStationNotProvidedPlaybackDataException(context, details.name)
		}
		val parsedResponse = parsePlaybackData(response.body(), details)
			?: throw RadioStationNotProvidedPlaybackDataException(context, details.name)

		val radioStationName = i18nBean.tRaw(
			i18nDynamicMod = I18nDynamicMod.ARG_OPTION_MOD,
			args = arrayOf("radio", details.name),
			lang = context.guildLanguage,
		)
		val messageBuilder = MessageEmbedBuilder(i18nBean, jdaColorStoreBean, context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.CURRENTLY_PLAYING_STREAM_CONTENT,
				args = mapOf("radioStationName" to radioStationName),
			)
			.setKeyValueField(I18nAudioSource.TRACK_NAME, parsedResponse.title)

		parsedResponse.nextPlay?.let {
			messageBuilder.setSpace()
			messageBuilder.setKeyValueField(I18nAudioSource.NEXT_TRACK_INDEX_MESS, it)
		}
		if (parsedResponse.elapsedNowSec != null && parsedResponse.trackDuration != null) {
			val bar = PercentageIndicatorBar(parsedResponse.elapsedNowSec, parsedResponse.trackDuration)
			messageBuilder.setValueField(bar.generateBar(), false)
		}
		parsedResponse.trackDuration?.let {
			messageBuilder.setKeyValueField(I18nAudioSource.TRACK_DURATION_TIME, dtFormat(it))
		}
		parsedResponse.toNextPlayDuration?.let {
			messageBuilder.setSpace()
			messageBuilder.setKeyValueField(I18nAudioSource.APPROX_TO_NEXT_TRACK_FROM_QUEUE, dtFormat(it))
		}
		return messageBuilder
			.setArtwork(parsedResponse.streamThumbnailUrl)
			.setFooter(I18nUtilSource.DATA_COMES_FROM, requestUri.host)
			.setColor(JdaColor.PRIMARY)
			.build()
	}

	/**
	 * Parses the JSON node to create a link for the external audio service provider.
	 *
	 * @param node The JSON node containing playback data.
	 * @param artistKey The key for accessing the artist name in the JSON.
	 * @return A formatted Markdown link to the track, or the author's name if the title is empty.
	 */
	protected fun parseToExternalAudioServiceProvider(node: JsonNode, artistKey: String): String {
		val title = node.getAsText("title") as String
		val author = node.getAsText(artistKey) as String
		if (title.isNotEmpty()) {
			val trackUri = URLEncoder.encode("$title $author", StandardCharsets.UTF_8)
			// remove all spaces to plus characters and encode to right URI syntax
			return mdLink(
				name = "$title - $author",
				link = redirectQuery.format(trackUri.replace("%20", "+")),
			)
		}
		return author
	}

	/**
	 * Parses the raw playback data response to create a [RadioPlaybackResponse].
	 *
	 * @param responseRaw The raw response string from the playback API.
	 * @param details The details of the radio station.
	 * @return A [RadioPlaybackResponse] object, or null if parsing fails.
	 */
	protected abstract fun parsePlaybackData(responseRaw: String, details: RadioStationDetails): RadioPlaybackResponse?
}
