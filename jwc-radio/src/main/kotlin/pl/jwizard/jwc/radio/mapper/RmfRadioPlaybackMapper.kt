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

@RadioPlaybackMapper(PlaybackProvider.RMF_GROUP)
class RmfRadioPlaybackMapper(
	environment: RadioPlaybackMapperEnvironment,
) : RadioPlaybackMapperHandler(environment) {

	override fun parsePlaybackData(
		responseRaw: String,
		radioStation: RadioStation,
	): RadioPlaybackResponse? {
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

	private fun getByOrder(
		jsonArray: ArrayNode,
		order: Int,
	) = jsonArray.find { it.getAsInt("order") == order }
}
