package pl.jwizard.jwc.audio.gateway.http

import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.arbjerg.lavalink.protocol.v4.Player
import dev.arbjerg.lavalink.protocol.v4.PlayerUpdate
import dev.arbjerg.lavalink.protocol.v4.json
import kotlinx.serialization.encodeToString
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import pl.jwizard.jwc.audio.gateway.node.NodeConfig
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal class AudioNodeRestClient(
	private val nodeConfig: NodeConfig,
	private val httpClient: OkHttpClient,
) {
	internal fun getPlayer(
		sessionId: String?,
		guildId: Long,
	): Mono<Player> = performRequest("/v4/sessions/$sessionId/players/$guildId").toMono()

	internal fun updatePlayer(
		sessionId: String?,
		player: PlayerUpdate,
		guildId: Long,
		noReplace: Boolean,
	): Mono<Player> =
		performRequest(
			url = "/v4/sessions/$sessionId/players/$guildId?noReplace=$noReplace",
			httpMethod = HttpMethod.PATCH,
			body = json.encodeToString(player).toRequestBody("application/json".toMediaType()),
		).toMono()

	internal fun destroyPlayer(
		sessionId: String?,
		guildId: Long,
	): Mono<Unit> = performRequest(
		url = "/v4/sessions/$sessionId/players/$guildId",
		httpMethod = HttpMethod.DELETE,
	).toMono()

	internal fun loadItem(
		decoded: String,
	): Mono<LoadResult> = performRequest(
		"/v4/loadtracks?identifier=${
			URLEncoder.encode(
				decoded,
				StandardCharsets.UTF_8
			)
		}"
	).toMono()

	private fun performRequest(
		url: String,
		httpMethod: HttpMethod = HttpMethod.GET,
		body: RequestBody? = null,
	): Call {
		val request = Request.Builder()
			.url(nodeConfig.httpUrl + url)
			.addHeader("Authorization", nodeConfig.password)
			.apply {
				nodeConfig.proxyVerificationToken?.let {
					addHeader(nodeConfig.proxyVerificationHeaderName, it)
				}
			}
			.method(httpMethod.name, body)
			.build()
		return httpClient.newCall(request)
	}

	private inline fun <reified T : Any> Call.toMono() = Mono.create {
		it.onCancel(this::cancel)
		enqueue(AsyncResponseCallback(it) { rawResponse -> json.decodeFromString<T>(rawResponse) })
	}
}
