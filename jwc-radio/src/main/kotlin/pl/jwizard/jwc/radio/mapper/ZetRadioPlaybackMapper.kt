package pl.jwizard.jwc.radio.mapper

import pl.jwizard.jwc.radio.RadioPlaybackMapper
import pl.jwizard.jwc.radio.RadioPlaybackMapperEnvironment
import pl.jwizard.jwc.radio.RadioPlaybackMapperHandler
import pl.jwizard.jwc.radio.RadioPlaybackResponse
import pl.jwizard.jwl.radio.PlaybackProvider
import pl.jwizard.jwl.radio.RadioStation
import pl.jwizard.jwl.util.ext.getAsNullableText
import java.time.Duration

@RadioPlaybackMapper(PlaybackProvider.ZET_GROUP)
class ZetRadioPlaybackMapper(
	environment: RadioPlaybackMapperEnvironment,
) : RadioPlaybackMapperHandler(environment) {

	override fun parsePlaybackData(
		responseRaw: String,
		radioStation: RadioStation,
	): RadioPlaybackResponse? {
		// remove "rdsData({" and "})"
		val jsonString = responseRaw
			.substring(responseRaw.indexOf("(") + 1, responseRaw.lastIndexOf(")"))
		val jsonObject = objectMapper.readTree(jsonString)

		val now = jsonObject.get("now")
		val rawDuration = now.getAsNullableText("duration")

		return RadioPlaybackResponse(
			title = parseToExternalAudioServiceProvider(now, "artist"),
			trackDuration = if (rawDuration == null || rawDuration.contains(" ")) {
				null
			} else {
				Duration.ofSeconds(rawDuration.toLong())
			},
			streamThumbnailUrl = now.getAsNullableText("img"),
		)
	}
}
