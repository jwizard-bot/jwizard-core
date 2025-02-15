package pl.jwizard.jwc.audio.gateway

import pl.jwizard.jwc.audio.gateway.event.player.*

interface AudioNodeListener {
	fun onTrackStart(event: KTrackStartEvent)

	fun onTrackEnd(event: KTrackEndEvent)

	fun onTrackStuck(event: KTrackStuckEvent)

	fun onTrackException(event: KTrackExceptionEvent)

	fun onCloseWsConnection(event: KWsClosedEvent)
}
