package pl.jwizard.jwc.audio.gateway.ws

import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwl.util.logger

internal class ReconnectAudioNodeTask(
	private val botId: Long,
	private val audioNodes: List<AudioNode>,
) : Runnable {
	companion object {
		private val log = logger<ReconnectAudioNodeTask>()
	}

	override fun run() {
		try {
			audioNodes.forEach { it.reconnectNode(botId) }
		} catch (ex: Exception) {
			log.error("Unable to reconnect with node. Cause: {}.", ex.message)
		}
	}
}
