/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio.mapper

import com.fasterxml.jackson.databind.node.ArrayNode
import pl.jwizard.jwc.core.util.fromNowToTime
import pl.jwizard.jwc.core.util.fromTimeToNow
import pl.jwizard.jwc.radio.RadioPlaybackMapper
import pl.jwizard.jwc.radio.RadioPlaybackMapperEnvironment
import pl.jwizard.jwc.radio.RadioPlaybackMapperHandler
import pl.jwizard.jwc.radio.RadioPlaybackResponse
import pl.jwizard.jwl.radio.PlaybackProvider
import pl.jwizard.jwl.radio.RadioStation
import pl.jwizard.jwl.util.ext.getAsInt
import pl.jwizard.jwl.util.ext.getAsLong
import pl.jwizard.jwl.util.ext.getAsNullableText
import java.time.Duration

/**
 * Mapper for RMF radio playback data. This class extracts and processes playback information from the RMF radio API
 * response.
 *
 * @property environment The environment providing the necessary beans for playback mapping.
 * @author Miłosz Gilga
 */
@RadioPlaybackMapper(PlaybackProvider.RMF_GROUP)
class RmfRadioPlaybackMapper(
	private val environment: RadioPlaybackMapperEnvironment
) : RadioPlaybackMapperHandler(environment) {

	/**
	 * Parses the raw playback data response to create a [RadioPlaybackResponse].
	 *
	 * This method processes the JSON array from the RMF radio API, extracting information about the current track and
	 * the next track, if available. It returns a [RadioPlaybackResponse] object populated with the relevant data.
	 *
	 * @param responseRaw The raw response string from the RMF radio API.
	 * @param radioStation Current selected [RadioStation] property.
	 * @return A [RadioPlaybackResponse] object containing the parsed playback data, or null if parsing fails.
	 */
	override fun parsePlaybackData(responseRaw: String, radioStation: RadioStation): RadioPlaybackResponse? {
		val jsonArray = objectMapper.readTree(responseRaw) as ArrayNode

		val current = getByOrder(jsonArray, 0)
		val next = getByOrder(jsonArray, 1)

		val coverImage = current?.getAsNullableText("coverBigUrl")
		val artistKey = "author"

		// check if data provided next playback, if not show only current audio playback content
		return when {
			current == null -> null
			next == null -> RadioPlaybackResponse(
				title = parseToExternalAudioServiceProvider(current, artistKey),
				streamThumbnailUrl = coverImage,
			)
			else -> {
				val currentTimestamp = current.getAsLong("timestamp")
				val nextTimestamp = next.getAsLong("timestamp")
				RadioPlaybackResponse(
					title = parseToExternalAudioServiceProvider(current, artistKey),
					trackDuration = Duration.ofSeconds(nextTimestamp - currentTimestamp),
					nextPlay = parseToExternalAudioServiceProvider(next, artistKey),
					toNextPlayDuration = fromTimeToNow(nextTimestamp),
					streamThumbnailUrl = coverImage,
					elapsedNowSec = fromNowToTime(currentTimestamp),
				)
			}
		}
	}

	/**
	 * Retrieves a track from the JSON array by its order. This method searches the provided JSON array for a track that
	 * matches the specified order.
	 *
	 * @param jsonArray The JSON array containing playback data.
	 * @param order The order of the track to retrieve (0 for current track, 1 for next track).
	 * @return The JSON node representing the track, or null if no match is found.
	 */
	private fun getByOrder(jsonArray: ArrayNode, order: Int) = jsonArray.find { it.getAsInt("order") == order }
}
