package pl.jwizard.jwc.audio.gateway.balancer.penalty

import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.*
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.pow

internal class Penalties(private val audioNode: AudioNode) {
	companion object {
		private val SDF = SimpleDateFormat("yyyy-MM-dd HH:mm")
	}

	// metrics store
	private val timeMap = LRUCache<String, MutableMap<MetricType, Int>>(100)

	fun handleTrackEvent(event: EmittedEvent) {
		val tracker = when (event) {
			is TrackStartEvent -> MetricType.LOAD_ATTEMPT
			is TrackEndEvent -> if (event.reason == TrackEndEvent.AudioTrackEndReason.LOAD_FAILED) {
				MetricType.LOAD_FAILED
			} else {
				null
			}
			is TrackExceptionEvent -> MetricType.TRACK_EXCEPTION
			is TrackStuckEvent -> MetricType.TRACK_STUCK
			else -> null
		}
		tracker?.let { trackMetric(it) }
	}

	fun calculateTotal(): Int {
		val stats = audioNode.stats
		val blockError = PenaltyFilteringResult.BLOCK.penalty

		if (!audioNode.available || stats == null) {
			return blockError
		}
		val metrics = getCurrentMetrics()
		val loadsAttempted = metrics[MetricType.LOAD_ATTEMPT] ?: 0
		val loadsFailed = metrics[MetricType.LOAD_FAILED] ?: 0

		if (loadsAttempted > 0 && loadsAttempted == loadsFailed) {
			return blockError
		}
		val cachedPlayingPlayers =
			audioNode.players.count { it.value.track != null && !it.value.paused }
		val playerPenalty = max(cachedPlayingPlayers, stats.playingPlayers)

		val cpuPenalty = (1.05.pow(100 * stats.cpu.systemLoad) * 10 - 10).toInt()
		val frames = stats.frameStats
		var deficitFramePenalty = 0
		var nullFramePenalty = 0

		if (frames != null && frames.deficit != -1) {
			deficitFramePenalty = (1.03f.pow(500f * (frames.deficit / 3000f)) * 600 - 600).toInt()
			nullFramePenalty = (1.03f.pow(500f * (frames.nulled / 3000f)) * 600 - 600).toInt()
			nullFramePenalty *= 2
		}
		val tracksStuck = metrics[MetricType.TRACK_STUCK] ?: 0
		val trackExceptions = metrics[MetricType.TRACK_EXCEPTION] ?: 0

		val trackStuckPenalty = tracksStuck * 100 - 100
		val trackExceptionPenalty = trackExceptions * 10 - 10
		val loadFailedPenalty = if (loadsFailed > 0) loadsFailed / loadsAttempted else 0

		return playerPenalty + cpuPenalty + deficitFramePenalty + nullFramePenalty +
			trackStuckPenalty + trackExceptionPenalty + loadFailedPenalty
	}

	internal fun resetMetrics() {
		timeMap.clear()
	}

	private fun trackMetric(metric: MetricType) {
		val timestamp = SDF.format(Date())
		val metricMap = timeMap.getOrPut(timestamp) { mutableMapOf() }

		val currentMetric = metricMap[metric] ?: 0
		metricMap[metric] = currentMetric + 1
	}

	private fun getCurrentMetrics(): Map<MetricType, Int> {
		val metricMap = mutableMapOf<MetricType, Int>()
		for (metric in timeMap.values) {
			for ((key, value) in metric) {
				metricMap[key] = (metricMap[key] ?: 0) + value
			}
		}
		return metricMap
	}
}
