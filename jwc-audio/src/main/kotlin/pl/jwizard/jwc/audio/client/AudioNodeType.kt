package pl.jwizard.jwc.audio.client

import pl.jwizard.jwac.node.NodePool

enum class AudioNodeType : NodePool {
	QUEUED,
	CONTINUOUS,
	;

	override val poolName = name
}
