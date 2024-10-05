/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio.mapper

import pl.jwizard.jwc.core.radio.RadioStationDetails
import pl.jwizard.jwc.core.util.ext.getAsText
import pl.jwizard.jwc.radio.RadioPlaybackMapper
import pl.jwizard.jwc.radio.RadioPlaybackMapperEnvironment
import pl.jwizard.jwc.radio.RadioPlaybackMapperHandler
import pl.jwizard.jwc.radio.RadioPlaybackResponse
import java.time.Duration

/**
 * Mapper for Zet Radio playback data. This class extracts and processes playback information from the Zet Radio API
 * response.
 *
 * @property environment The environment providing the necessary beans for playback mapping.
 * @author Miłosz Gilga
 */
@RadioPlaybackMapper
class ZetRadioPlaybackMapper(
	private val environment: RadioPlaybackMapperEnvironment
) : RadioPlaybackMapperHandler(environment) {

	/**
	 * Parses the raw playback data response to create a [RadioPlaybackResponse].
	 *
	 * This method processes the raw API response from Zet Radio, extracting information about the currently playing
	 * track and its duration. It returns a [RadioPlaybackResponse] object populated with the relevant data.
	 *
	 * @param responseRaw The raw response string from the Zet Radio API.
	 * @param details The details of the radio station.
	 * @return A [RadioPlaybackResponse] object containing the parsed playback data, or null if parsing fails.
	 */
	override fun parsePlaybackData(responseRaw: String, details: RadioStationDetails): RadioPlaybackResponse? {
		// remove "rdsData({" and "})"
		val jsonString = responseRaw.substring(responseRaw.indexOf("(") + 1, responseRaw.lastIndexOf(")"))
		val jsonObject = objectMapper.readTree(jsonString)

		val now = jsonObject.get("now")
		val rawDuration = now.getAsText("duration")

		return RadioPlaybackResponse(
			title = parseToExternalAudioServiceProvider(now, "artist"),
			trackDuration = if (rawDuration == null || rawDuration.contains(" ")) {
				null
			} else {
				Duration.ofSeconds(rawDuration.toLong())
			},
			streamThumbnailUrl = now.getAsText("img"),
		)
	}
}
