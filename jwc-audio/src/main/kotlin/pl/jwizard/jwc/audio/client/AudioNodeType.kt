package pl.jwizard.jwc.audio.client

import pl.jwizard.jwc.audio.gateway.node.NodePool

enum class AudioNodeType : NodePool {
	QUEUED,
	CONTINUOUS,
	;

	override val poolName = name
}
