package pl.jwizard.jwc.audio.gateway.balancer

import pl.jwizard.jwc.audio.gateway.balancer.penalty.PenaltyProvider
import pl.jwizard.jwc.audio.gateway.balancer.region.VoiceRegion
import pl.jwizard.jwc.audio.gateway.node.AudioNode

abstract class LoadBalancer {
	// penalty providers that can be used to calculate penalties for the audio nodes
	// penalties influence the selection of the best audio node
	protected val penaltyProviders = mutableListOf<PenaltyProvider>()

	fun addPenaltyProvider(penaltyProvider: PenaltyProvider) {
		penaltyProviders.add(penaltyProvider)
	}

	@Suppress("unused")
	fun removePenaltyProvider(penaltyProvider: PenaltyProvider) {
		penaltyProviders.remove(penaltyProvider)
	}

	// selects an audio node from the given list based on the provided region and guild ID
	// selection process considers penalties from all registered penalty providers
	abstract fun selectNode(
		audioNodes: List<AudioNode>,
		region: VoiceRegion?,
		guildId: Long?,
	): AudioNode
}
