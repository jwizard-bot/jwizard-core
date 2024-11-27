/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.client

import pl.jwizard.jwac.AudioNodeListener
import pl.jwizard.jwac.event.player.*
import pl.jwizard.jwc.audio.manager.MusicManagersBean
import pl.jwizard.jwc.audio.scheduler.AudioScheduleHandler
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger

/**
 * Adapter class that listens to audio node events and delegates audio management tasks to the appropriate music
 * managers and schedulers. Handles events like track start, track end, and WebSocket disconnection to ensure smooth
 * audio playback.
 *
 * @property musicManagers Manages music-related resources and states across multiple guilds.
 * @property jdaShardManager Manages multiple shards of the JDA bot, responsible for handling Discord API interactions.
 * @author Miłosz Gilga
 */
@SingletonComponent
class AudioNodeListenerAdapter(
	private val musicManagers: MusicManagersBean,
	private val jdaShardManager: JdaShardManagerBean,
) : AudioNodeListener {

	companion object {
		private val log = logger<AudioNodeListenerAdapter>()
	}

	/**
	 * Triggered when a track starts playing on an audio node. It delegates the event to the audio scheduler to handle
	 * the start of the track.
	 *
	 * @param event The event containing track and node information.
	 */
	override fun onTrackStart(event: KTrackStartEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioStart(event.track, event.audioNode)
	}

	/**
	 * Triggered when a track ends playing on an audio node. It delegates the event to the audio scheduler to handle
	 * post-track processing.
	 *
	 * @param event The event containing track, node, and end reason information.
	 */
	override fun onTrackEnd(event: KTrackEndEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioEnd(event.track, event.audioNode, event.endReason)
	}

	/**
	 * Triggered when a track gets stuck on an audio node. It delegates the event to the audio scheduler to handle the
	 * stuck track scenario.
	 *
	 * @param event The event containing track and node information.
	 */
	override fun onTrackStuck(event: KTrackStuckEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioStuck(event.track, event.audioNode)
	}

	/**
	 * Triggered when an exception occurs during track playback on an audio node. It delegates the event to the audio
	 * scheduler to handle the exception.
	 *
	 * @param event The event containing track, node, and exception information.
	 */
	override fun onTrackException(event: KTrackExceptionEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioException(event.track, event.audioNode, event.exception)
	}

	/**
	 * Handles the WebSocket closure event, logging the details of the closure.
	 *
	 * @param event The event containing details about the WebSocket closure.
	 */
	override fun onCloseWsConnection(event: KWsClosedEvent) {
		log.debug("(node: {}) Close WS connection with code: {}. ", event.audioNode, event.code)
	}

	/**
	 * Retrieves the audio scheduler for the given guild ID. The scheduler is responsible for managing the audio queue
	 * and playback.
	 *
	 * @param guildId The ID of the guild for which to retrieve the scheduler.
	 * @return The [AudioScheduleHandler] instance or null if no scheduler is available.
	 */
	private fun getAudioScheduler(guildId: Long): AudioScheduleHandler? {
		val musicManager = musicManagers.getCachedMusicManager(guildId)
		return musicManager?.state?.audioScheduler
	}
}
