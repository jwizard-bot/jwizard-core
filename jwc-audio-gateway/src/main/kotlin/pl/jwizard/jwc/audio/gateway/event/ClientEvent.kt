package pl.jwizard.jwc.audio.gateway.event

import pl.jwizard.jwc.audio.gateway.node.AudioNode

abstract class ClientEvent {
	abstract val audioNode: AudioNode
}
