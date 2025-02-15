package pl.jwizard.jwc.audio.gateway.balancer.penalty

import pl.jwizard.jwc.audio.gateway.balancer.region.RegionGroup
import pl.jwizard.jwc.audio.gateway.balancer.region.VoiceRegion
import pl.jwizard.jwc.audio.gateway.node.AudioNode

internal class VoiceRegionPenaltyProvider : PenaltyProvider {
	override fun getPenalty(audioNode: AudioNode, region: VoiceRegion?): Int {
		val filter = audioNode.config.regionGroup
		// if region is not defined, omit add penalties
		if (region == null || region == VoiceRegion.UNKNOWN || filter == RegionGroup.UNKNOWN) {
			return 0
		}
		val penaltyResult = if (region.group == filter) {
			// if region is match with server region, pass
			PenaltyFilteringResult.PASS
		} else {
			// otherwise soft block (block until better node is not connected)
			PenaltyFilteringResult.SOFT_BLOCK
		}
		return penaltyResult.penalty
	}
}
