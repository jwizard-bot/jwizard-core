package pl.jwizard.jwc.audio.gateway.player.track

import kotlinx.serialization.json.jsonObject
import pl.jwizard.jwc.audio.gateway.util.fromJsonElement
import pl.jwizard.jwc.audio.gateway.util.toJsonElement
import pl.jwizard.jwc.audio.gateway.util.toJsonObject
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwc.core.util.millisToDTF
import dev.arbjerg.lavalink.protocol.v4.Track as ProtocolTrack

// wrapper for WS protocol Track
class Track(private var protocolTrack: ProtocolTrack) {
	val encoded = protocolTrack.encoded
	val uri = protocolTrack.info.uri
	val thumbnailUrl = protocolTrack.info.artworkUrl
	val duration = protocolTrack.info.length

	val qualifier
		get() = "\"${getTitle(normalized = true)}\""

	val mdTitleLink
		get() = mdLink(getTitle(normalized = true), uri)

	val titleWithDuration
		get() = "(${millisToDTF(duration)}): ${getTitle(normalized = true)}"

	val mdTitleLinkWithDuration
		get() = "(${millisToDTF(duration)}): $mdTitleLink"

	internal val userData = protocolTrack.userData

	val audioSender
		get() = fromJsonElement<AudioSender>(protocolTrack.userData)

	fun setSenderData(userData: AudioSender?) {
		val jsonElement = toJsonElement(userData)
		protocolTrack = protocolTrack.copyWithUserData(jsonElement.jsonObject)
	}

	fun getTitle(normalized: Boolean = false): String {
		var title = protocolTrack.info.title
		if (normalized) {
			title = "${title.replace("*", "")} (${protocolTrack.info.author})"
		}
		return title
	}

	// deep copy of current audio track (useful for infinite loop)
	fun makeClone() = Track(
		protocolTrack.copy(
			info = protocolTrack.info.copy(position = 0L),
			userData = toJsonObject(protocolTrack.userData)
		)
	)
}
