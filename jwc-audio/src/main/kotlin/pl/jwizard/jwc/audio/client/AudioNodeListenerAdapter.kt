package pl.jwizard.jwc.audio.client

import pl.jwizard.jwac.AudioNodeListener
import pl.jwizard.jwac.event.player.*
import pl.jwizard.jwc.audio.manager.MusicManagersBean
import pl.jwizard.jwc.audio.scheduler.AudioScheduleHandler
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger

@SingletonComponent
class AudioNodeListenerAdapter(private val musicManagers: MusicManagersBean) : AudioNodeListener {

	companion object {
		private val log = logger<AudioNodeListenerAdapter>()
	}

	override fun onTrackStart(event: KTrackStartEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioStart(event.track, event.audioNode)
	}

	override fun onTrackEnd(event: KTrackEndEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioEnd(event.track, event.audioNode, event.endReason)
	}

	override fun onTrackStuck(event: KTrackStuckEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioStuck(event.track, event.audioNode)
	}

	override fun onTrackException(event: KTrackExceptionEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioException(event.track, event.audioNode, event.exception)
	}

	override fun onCloseWsConnection(event: KWsClosedEvent) {
		log.debug("(node: {}) Close WS connection with code: {}. ", event.audioNode, event.code)
	}

	private fun getAudioScheduler(guildId: Long): AudioScheduleHandler? {
		val musicManager = musicManagers.getCachedMusicManager(guildId)
		return musicManager?.state?.audioScheduler
	}
}
