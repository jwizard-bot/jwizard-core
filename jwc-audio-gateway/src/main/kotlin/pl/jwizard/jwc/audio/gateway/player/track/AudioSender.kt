package pl.jwizard.jwc.audio.gateway.player.track

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

// json dto with audio sender details
// serialized and deserialized by Lavalink protocol
data class AudioSender @JsonCreator constructor(@JsonProperty("authorId") val authorId: Long)
