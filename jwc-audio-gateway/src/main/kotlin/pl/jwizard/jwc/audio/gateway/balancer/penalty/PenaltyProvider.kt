package pl.jwizard.jwc.audio.gateway.balancer.penalty

import pl.jwizard.jwc.audio.gateway.balancer.region.VoiceRegion
import pl.jwizard.jwc.audio.gateway.node.AudioNode

interface PenaltyProvider {
	fun getPenalty(audioNode: AudioNode, region: VoiceRegion?): Int
}
