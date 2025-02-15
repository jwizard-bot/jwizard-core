package pl.jwizard.jwc.audio.gateway.balancer

import pl.jwizard.jwc.audio.gateway.balancer.penalty.VoiceRegionPenaltyProvider
import pl.jwizard.jwc.audio.gateway.balancer.region.VoiceRegion
import pl.jwizard.jwc.audio.gateway.node.AudioNode

class DefaultLoadBalancer : LoadBalancer() {
	init {
		addPenaltyProvider(VoiceRegionPenaltyProvider())
	}

	override fun selectNode(
		audioNodes: List<AudioNode>,
		region: VoiceRegion?,
		guildId: Long?,
	): AudioNode {
		if (audioNodes.size == 1) {
			// if is only one node, do not load balancing
			val audioNode = audioNodes.first()
			if (!audioNode.available) {
				// MUST BE AT LEAST ONE AVAILABLE AUDIO NODE
				throw IllegalStateException("Audio node: $audioNode is unavailable.")
			}
			return audioNode
		}
		return audioNodes
			.filter { it.available }
			// get node with minimum penalty value (calculated by penalty provider)
			.minByOrNull {
				it.penalties.calculateTotal() + penaltyProviders.sumOf { p ->
					p.getPenalty(
						it,
						region
					)
				}
			}
			?: throw IllegalStateException("Unable to find any available audio node.")
	}
}
