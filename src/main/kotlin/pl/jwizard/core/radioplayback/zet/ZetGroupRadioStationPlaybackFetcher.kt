/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback.zet

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.apache.commons.lang3.StringUtils
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

@Component(value = "zet+melo+anty")
class ZetGroupRadioStationPlaybackFetcher(
	private val httpClient: HttpClient,
	private val gsonParser: Gson
) : RadioStationPlaybackFetcher, AbstractLoggingBean(ZetGroupRadioStationPlaybackFetcher::class) {

	override fun fetchData(stationSlug: String): RadioPlaybackResponseData? {
		val api = ZetGroupApiMapper.getTypeForSlug(stationSlug) ?: return null
		val response = httpClient.send(
			HttpRequest.newBuilder()
				.uri(URI.create(api.parseToUrl()))
				.build(),
			HttpResponse.BodyHandlers.ofString()
		)
		if (response.statusCode() != 200) {
			return null
		}
		val rawString = response.body()
		// remove "rdsData({" and "})"
		val jsonString = rawString.substring(rawString.indexOf("(") + 1, rawString.lastIndexOf(")"))
		val jsonObject = gsonParser.fromJson(jsonString, JsonObject::class.java)
		// get now object
		val apiResponse = ZetGroupApiResponse(jsonObject.getAsJsonObject("now"))
		// check, if string has space, if space exist, return null otherwise parse to long
		val duration = if (apiResponse.duration.contains(StringUtils.SPACE)) {
			null
		} else {
			DateUtils.convertSecToDTF(apiResponse.duration.toLong())
		}
		val playbackData = RadioPlaybackResponseData(
			title = Formatter.createYoutubeRedirectSearchLink(apiResponse.title, apiResponse.artist),
			trackDuration = duration,
			streamThumbnailUrl = apiResponse.img,
			providedBy = api.getProvider(),
		)
		log.debug(
			"Successfully fetched radio: {} defails from API: {}. Data: {}",
			stationSlug,
			api.parseToUrl(),
			playbackData
		)
		return playbackData
	}
}
