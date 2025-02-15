package pl.jwizard.jwc.audio.gateway.http

import dev.arbjerg.lavalink.protocol.v4.Error
import dev.arbjerg.lavalink.protocol.v4.json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import reactor.core.publisher.MonoSink
import java.io.IOException

internal class AsyncResponseCallback<T : Any>(
	private val emitter: MonoSink<T>,
	private val onParse: (String) -> T,
) : Callback {
	override fun onFailure(call: Call, e: IOException) {
		emitter.error(e)
	}

	override fun onResponse(call: Call, response: Response) {
		response.body?.use {
			val responseStr = it.string()
			if (response.code > 299) {
				val error = json.decodeFromString<Error>(responseStr)
				emitter.error(RestException(error))
				return
			}
			if (response.code == 204) {
				emitter.success()
				return
			}
			val parsed = onParse(responseStr)
			emitter.success(parsed)
		}
	}
}
