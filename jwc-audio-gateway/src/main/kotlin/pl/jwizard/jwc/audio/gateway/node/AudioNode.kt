package pl.jwizard.jwc.audio.gateway.node

import dev.arbjerg.lavalink.protocol.v4.PlayerUpdate
import dev.arbjerg.lavalink.protocol.v4.Stats
import okhttp3.OkHttpClient
import pl.jwizard.jwc.audio.gateway.AudioClient
import pl.jwizard.jwc.audio.gateway.ReactiveEventPublisher
import pl.jwizard.jwc.audio.gateway.balancer.penalty.Penalties
import pl.jwizard.jwc.audio.gateway.event.ClientEvent
import pl.jwizard.jwc.audio.gateway.http.AudioNodeRestClient
import pl.jwizard.jwc.audio.gateway.http.RestException
import pl.jwizard.jwc.audio.gateway.player.AudioPlayer
import pl.jwizard.jwc.audio.gateway.player.AudioPlayerUpdateBuilder
import pl.jwizard.jwc.audio.gateway.ws.AudioWsClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

// node represents single audio server
class AudioNode(
	private val nodeConfig: NodeConfig,
	private val audioClient: AudioClient,
	private val instanceName: String,
) : Closeable {
	val name = nodeConfig.name
	val pool = nodeConfig.pool
	val config = nodeConfig

	internal val publisher = ReactiveEventPublisher<ClientEvent>()
	internal val penalties = Penalties(this)

	// players bound with selected guild id
	internal val players = ConcurrentHashMap<Long, AudioPlayer>()

	// is node available
	internal var available = false
	internal var sessionId: String? = null

	var stats: Stats? = null
		internal set

	private val httpClient = OkHttpClient.Builder()
		.callTimeout(nodeConfig.httpTimeout, TimeUnit.MILLISECONDS)
		.pingInterval(5, TimeUnit.MINUTES)
		.build()

	private val rest = AudioNodeRestClient(nodeConfig, httpClient)
	private val ws = AudioWsClient(nodeConfig, httpClient, this, publisher, audioClient)

	internal fun connect(botId: Long) {
		ws.connect(instanceName, botId, sessionId)
	}

	internal fun reconnectNode(botId: Long) {
		ws.reconnect(instanceName, botId, sessionId)
	}

	fun getCachedPlayer(guildId: Long) = players[guildId]

	fun getPlayer(guildId: Long) = withNodeAvailableCheck {
		if (players.containsKey(guildId)) {
			players[guildId].toMono()
		} else {
			rest.getPlayer(sessionId, guildId).map { AudioPlayer(this, it) }
				.onErrorResume {
					if (it is RestException && it.code == 404) {
						createOrUpdatePlayer(guildId)
					} else {
						it.toMono()
					}
				}
				.doOnSuccess { players[it.guildId] = it }
		}
	}

	fun createOrUpdatePlayer(guildId: Long) = AudioPlayerUpdateBuilder(guildId, this)

	fun updatePlayer(guildId: Long, playerUpdate: PlayerUpdate, noReplace: Boolean) =
		withNodeAvailableCheck {
			rest.updatePlayer(sessionId, playerUpdate, guildId, noReplace)
				.map { AudioPlayer(this, it) }
				.doOnSuccess { players[guildId] = it }
		}

	fun destroyPlayer(guildId: Long) = withNodeAvailableCheck {
		rest.destroyPlayer(sessionId, guildId)
			.doOnSuccess { removeCachedPlayer(guildId) }
	}

	fun destroyPlayerAndLink(guildId: Long) = withNodeAvailableCheck {
		rest.destroyPlayer(sessionId, guildId).doOnSuccess {
			removeCachedPlayer(guildId)
			audioClient.removeDestroyedLink(guildId)
		}
	}

	fun loadItem(decoded: String) = withNodeAvailableCheck { rest.loadItem(decoded) }

	internal fun removeCachedPlayer(guildId: Long) {
		players.remove(guildId)
	}

	internal fun transferOrphansToSelf() {
		audioClient.transferOrphansTo(this)
	}

	internal fun inNodePool(pool: NodePool) = this.pool.poolName == pool.poolName

	private fun <T : Any> withNodeAvailableCheck(onPerformRequest: () -> Mono<T>): Mono<T> {
		if (!available) {
			return Mono.error(IllegalStateException("Audio node is not available"))
		}
		return onPerformRequest()
	}

	override fun close() {
		available = false
		ws.close()
		httpClient.dispatcher.executorService.shutdown()
		httpClient.connectionPool.evictAll()
		httpClient.cache?.close()
		publisher.dispose()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (javaClass != other?.javaClass) {
			return false
		}
		other as AudioNode
		return available == other.available && nodeConfig == other.nodeConfig
			&& sessionId == other.sessionId
	}

	override fun hashCode(): Int {
		var result = nodeConfig.hashCode()
		result = 31 * result + sessionId.hashCode()
		result = 31 * result + available.hashCode()
		return result
	}

	override fun toString() = "${nodeConfig.name} (pool: ${nodeConfig.pool})"
}
