package pl.jwizard.jwc.radio

import com.fasterxml.jackson.databind.JsonNode
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.jda.embed.PercentageIndicatorBar
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMessage
import pl.jwizard.jwc.core.util.dtFormat
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.exception.radio.RadioStationNotProvidedPlaybackDataException
import pl.jwizard.jwl.radio.RadioStation
import pl.jwizard.jwl.util.ext.getAsText
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

abstract class RadioPlaybackMapperHandler(
	environment: RadioPlaybackMapperEnvironment,
) : RadioPlaybackMessage {

	private val i18n = environment.i18nBean
	private val jdaColorStore = environment.jdaColorsCache
	protected val objectMapper = environment.objectMapper

	private val redirectQuery = environment.environmentBean
		.getProperty<String>(BotProperty.RADIO_PLAYBACK_EXTENDED_LINK)

	private val httpClient = HttpClient.newHttpClient()

	override fun createPlaybackDataMessage(
		radioStation: RadioStation,
		context: CommandBaseContext,
	): MessageEmbed {
		if (radioStation.playbackApiUrl == null) {
			throw RadioStationNotProvidedPlaybackDataException(context, radioStation)
		}
		val requestUri = URI.create(radioStation.playbackApiUrl!!)
		val httpRequest = HttpRequest.newBuilder()
			.uri(requestUri)
			.build()

		val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
		if (response.statusCode() != 200) {
			throw RadioStationNotProvidedPlaybackDataException(context, radioStation)
		}
		val parsedResponse = parsePlaybackData(response.body(), radioStation)
			?: throw RadioStationNotProvidedPlaybackDataException(context, radioStation)

		val messageBuilder = MessageEmbedBuilder(i18n, jdaColorStore, context)
			.setTitle(
				i18nLocaleSource = I18nResponseSource.CURRENTLY_PLAYING_STREAM_CONTENT,
				args = mapOf("radioStationName" to i18n.t(radioStation, context.language)),
			)
			.setKeyValueField(I18nAudioSource.TRACK_NAME, parsedResponse.title)

		parsedResponse.nextPlay?.let {
			messageBuilder.setSpace()
			messageBuilder.setKeyValueField(I18nAudioSource.NEXT_TRACK_INDEX_MESS, it)
		}
		if (parsedResponse.elapsedNowSec != null && parsedResponse.trackDuration != null) {
			val bar = PercentageIndicatorBar(
				parsedResponse.elapsedNowSec.seconds,
				parsedResponse.trackDuration.seconds,
			)
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

	protected fun parseToExternalAudioServiceProvider(node: JsonNode, artistKey: String): String {
		val title = node.getAsText("title")
		val author = node.getAsText(artistKey)
		if (title.isNotEmpty()) {
			val trackUri = URLEncoder.encode("$title $author", StandardCharsets.UTF_8)
			// replace all spaces to plus characters and encode to right URI syntax
			return mdLink(
				name = "$title - $author",
				link = redirectQuery.format(trackUri.replace("%20", "+")),
			)
		}
		return author
	}

	protected abstract fun parsePlaybackData(
		responseRaw: String,
		radioStation: RadioStation
	): RadioPlaybackResponse?
}
