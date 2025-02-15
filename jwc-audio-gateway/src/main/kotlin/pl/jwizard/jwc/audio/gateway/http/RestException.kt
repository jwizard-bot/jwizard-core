package pl.jwizard.jwc.audio.gateway.http

import dev.arbjerg.lavalink.protocol.v4.Error

internal class RestException(error: Error) : Exception(error.message) {
	val code = error.status
}
