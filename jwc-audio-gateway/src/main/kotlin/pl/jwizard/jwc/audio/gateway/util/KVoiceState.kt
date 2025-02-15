package pl.jwizard.jwc.audio.gateway.util

import dev.arbjerg.lavalink.protocol.v4.VoiceState

internal fun VoiceState?.isEmpty() = if (this != null) {
	this.token.isBlank() || this.endpoint.isBlank() || this.sessionId.isBlank()
} else {
	true
}
