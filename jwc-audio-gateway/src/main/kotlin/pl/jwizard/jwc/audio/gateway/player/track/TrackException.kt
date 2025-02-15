package pl.jwizard.jwc.audio.gateway.player.track

import dev.arbjerg.lavalink.protocol.v4.Exception
import dev.arbjerg.lavalink.protocol.v4.Exception.Severity

data class TrackException(
	val message: String?,
	val severity: Severity,
	val cause: String,
) {
	companion object {
		fun fromProtocol(
			exception: Exception,
		) = TrackException(exception.message, exception.severity, exception.cause)
	}
}
