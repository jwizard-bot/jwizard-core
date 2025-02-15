package pl.jwizard.jwc.audio.gateway.ws

import dev.arbjerg.lavalink.protocol.v4.Message
import dev.arbjerg.lavalink.protocol.v4.Message.*
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.*
import dev.arbjerg.lavalink.protocol.v4.json
import okhttp3.*
import pl.jwizard.jwc.audio.gateway.AudioClient
import pl.jwizard.jwc.audio.gateway.ReactiveEventPublisher
import pl.jwizard.jwc.audio.gateway.event.ClientEvent
import pl.jwizard.jwc.audio.gateway.event.KPlayerUpdateEvent
import pl.jwizard.jwc.audio.gateway.event.KReadyEvent
import pl.jwizard.jwc.audio.gateway.event.KStatsEvent
import pl.jwizard.jwc.audio.gateway.event.player.*
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.node.NodeConfig
import pl.jwizard.jwl.util.logger
import java.io.Closeable
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

class AudioWsClient(
	private val nodeConfig: NodeConfig,
	private val httpClient: OkHttpClient,
	private val audioNode: AudioNode,
	private val eventPublisher: ReactiveEventPublisher<ClientEvent>,
	private val audioClient: AudioClient,
) : WebSocketListener(), Closeable {
	companion object {
		private val log = logger<AudioWsClient>()
	}

	// is websocket open
	private var open = false

	// websocket allows to reconnect
	private var mayReconnect = true

	// time after last reconnect attempts (in millis)
	private var lastReconnectAttempt = 0L
	private var reconnectAttempts = 0

	private var webSocket: WebSocket? = null
	private val audioWsEvent = AudioWsEventImpl(audioNode, audioClient)

	fun connect(instanceName: String, botId: Long, sessionId: String?) {
		webSocket?.let {
			// before establish new connection, close previous
			it.close(WsCode.NORMAL, "New WS connection requested.")
			it.cancel()
		}
		val request = Request.Builder()
			.url("${nodeConfig.wsUrl}/v4/websocket")
			.addHeader("Authorization", nodeConfig.password)
			.addHeader("Client-Name", "jwc/$instanceName")
			.addHeader("User-Id", botId.toString())
			.apply { sessionId?.let { addHeader("Session-Id", it) } }
			.build()

		webSocket = httpClient.newWebSocket(request, this)
	}

	fun reconnect(instanceName: String, botId: Long, sessionId: String?) {
		// elapsed time after last reconnect attempt
		val elapsedTime = System.currentTimeMillis() - lastReconnectAttempt
		if (webSocket != null && !open && elapsedTime > calcReconnectInterval(false) && mayReconnect) {
			// make reconnect only if connection is closed and elapsed time is greater than reconnect
			// interval (interval increases with further reconnection attempts
			lastReconnectAttempt = System.currentTimeMillis()
			reconnectAttempts += 1
			connect(instanceName, botId, sessionId)
		}
	}

	override fun onOpen(webSocket: WebSocket, response: Response) {
		log.info("Audio node: {} has been connected.", nodeConfig)
		open = true
		reconnectAttempts = 0 // reset reconnection attempts
	}

	override fun onMessage(webSocket: WebSocket, text: String) {
		val event = json.decodeFromString<Message>(text)
		log.debug("-> {}", text)

		when (event.op) {
			Op.Ready -> audioWsEvent.onReady(event as ReadyEvent)
			Op.Stats -> audioWsEvent.onStats(event as StatsEvent)
			Op.PlayerUpdate -> audioWsEvent.onPlayerUpdate(event as PlayerUpdateEvent)
			Op.Event -> {
				event as EmittedEvent
				when (event) {
					is TrackStartEvent -> audioWsEvent.onTrackStart(event)
					is TrackEndEvent -> audioWsEvent.onTrackEnd(event)
					is WebSocketClosedEvent -> audioWsEvent.onWsClosed(event)
					else -> Unit
				}
				audioNode.penalties.handleTrackEvent(event)
			}
			else -> log.error("Unknown event: {} on node: {}.", nodeConfig, text)
		}
		val clientEvent = when (event) {
			is ReadyEvent -> KReadyEvent.fromProtocol(audioNode, event)
			is TrackStartEvent -> KTrackStartEvent.fromProtocol(audioNode, event)
			is TrackEndEvent -> KTrackEndEvent.fromProtocol(audioNode, event)
			is TrackExceptionEvent -> KTrackExceptionEvent.fromProtocol(audioNode, event)
			is TrackStuckEvent -> KTrackStuckEvent.fromProtocol(audioNode, event)
			is WebSocketClosedEvent -> KWsClosedEvent.fromProtocol(audioNode, event)
			is PlayerUpdateEvent -> KPlayerUpdateEvent.fromProtocol(audioNode, event)
			is StatsEvent -> KStatsEvent.fromProtocol(audioNode, event)
		}
		eventPublisher.publishWithException(clientEvent)
	}

	override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
		val reconnectSec = calcReconnectInterval()
		when (t) {
			is EOFException -> log.warn("Disconnected from: {}, trying to reconnect.", nodeConfig)
			is SocketTimeoutException -> log.warn(
				"Disconnect from: {} (timeout), trying to reconnect.",
				nodeConfig
			)
			is ConnectException -> log.warn(
				"Failed to connect with: {}. Retrying connection attempt in {}s.",
				nodeConfig,
				reconnectSec,
			)
			is SocketException -> {
				if (open) {
					log.warn(
						"Socket error on: {}, Retrying connection attempt in {}s",
						nodeConfig,
						reconnectSec
					)
				} else {
					log.warn("Socket error on: {}. Socket closed.", nodeConfig)
				}
			}
			else -> log.warn("Unknown error on WS connection with node: {}.", nodeConfig)
		}
		log.warn("Error cause: {}.", t.message)

		audioNode.available = false
		open = false

		audioClient.onNodeDisconnected(audioNode)
	}

	override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
		audioNode.available = false
		audioClient.onNodeDisconnected(audioNode)
		// if websocket closed abnormally, do not reconnect
		val disconnectType = if (WsCode.NORMAL.isEqual(code)) {
			mayReconnect = false
			"normally"
		} else {
			"abnormally"
		}
		log.info(
			"Connection with node: {} was closed {} with reason: {} and code: {}.",
			nodeConfig,
			disconnectType,
			reason,
			code,
		)
	}

	override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
		if (mayReconnect) {
			log.info("Attempt to re-connect with: {} in time: {}s.", nodeConfig, calcReconnectInterval())
			audioNode.available = false
			open = false
		}
		log.debug("Stop sending ping to node: {}.", nodeConfig)
	}

	override fun close() {
		mayReconnect = false
		open = false
		webSocket?.close(WsCode.NORMAL, "WS client shutdown.")
		webSocket?.cancel()
	}

	private fun calcReconnectInterval(toMillis: Boolean = true): Int {
		var thresholdMillis = reconnectAttempts * 2000 - 200
		if (toMillis) {
			thresholdMillis /= 1000
		}
		return thresholdMillis
	}

	private fun WebSocket.close(wsErrorCode: WsCode, message: String) {
		close(wsErrorCode.code, message)
	}
}
