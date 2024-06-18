/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback.rmf

import com.google.gson.Gson
import com.google.gson.JsonArray
import org.springframework.stereotype.Component
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.radioplayback.RadioPlaybackResponseData
import pl.jwizard.core.radioplayback.RadioStationPlaybackFetcher
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component("rmf-fm+rmf-maxx")
class RmfRadioStationPlaybackFetcher(
	private val httpClient: HttpClient,
	private val gsonParser: Gson
) : RadioStationPlaybackFetcher, AbstractLoggingBean(RmfRadioStationPlaybackFetcher::class) {

	override fun fetchData(stationSlug: String): RadioPlaybackResponseData? {
		// get API mapping type base station slug, if not found show not support exception
		val api = RmfApiMapper.getTypeForSlug(stationSlug) ?: return null
		val response = httpClient.send(
			HttpRequest.newBuilder()
				.uri(URI.create(api.parseToUrl()))
				.build(),
			HttpResponse.BodyHandlers.ofString()
		)
		if (response.statusCode() != 200) {
			return null
		}
		val jsonArray = gsonParser.fromJson(response.body(), JsonArray::class.java)
		val current = getByOrder(jsonArray, 0) as RmfApiResponse
		val next = getByOrder(jsonArray, 1)
		// check if data provided next playback, if not show only current audio playback content
		val playbackData = if (next == null) {
			RadioPlaybackResponseData(
				title = parseToNonTracksAudioContent(current),
				streamThumbnailUrl = current.coverBigUrl,
				providedBy = api.getProvider(),
			)
		} else {
			RadioPlaybackResponseData(
				title = parseToNonTracksAudioContent(current),
				trackDuration = DateUtils.convertSecToDTF(next.timestamp - current.timestamp),
				nextPlay = parseToNonTracksAudioContent(next),
				toNextPlayDuration = DateUtils.getTimeToDuration(next.timestamp),
				streamThumbnailUrl = current.coverBigUrl,
				percentageBar = Formatter.createPercentageRepresentation(
					System.currentTimeMillis() / 1000 - current.timestamp, // current audio exp seconds
					next.timestamp - current.timestamp // end timestamp (total long of audio content)
				),
				providedBy = api.getProvider(),
			)
		}
		log.debug(
			"Successfully fetched radio: {} defails from API: {}. Data: {}",
			stationSlug,
			api.parseToUrl(),
			playbackData
		)
		return playbackData
	}

	private fun getByOrder(jsonArray: JsonArray, order: Int): RmfApiResponse? = jsonArray
		.find { it.asJsonObject["order"].asInt == order }
		?.let { RmfApiResponse(it) }

	private fun parseToNonTracksAudioContent(response: RmfApiResponse): String = if (response.title.isEmpty()) {
		response.author
	} else {
		Formatter.createYoutubeRedirectSearchLink(response.title, response.author)
	}
}
