package pl.jwizard.jwc.audio.gateway.event.player

import pl.jwizard.jwc.audio.gateway.event.ClientEvent

abstract class EmittedEvent : ClientEvent() {
	abstract val guildId: Long
}
