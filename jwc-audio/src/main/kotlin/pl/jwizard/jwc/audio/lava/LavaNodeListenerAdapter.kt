/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.lava

import dev.arbjerg.lavalink.client.event.*
import pl.jwizard.jwc.audio.lava.LavalinkClientBean.Companion.WS_SESSION_INVALID
import pl.jwizard.jwc.audio.manager.MusicManagersBean
import pl.jwizard.jwc.audio.scheduler.AudioScheduleHandler
import pl.jwizard.jwc.core.jda.spi.JdaInstance
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * Adapter class that listens to Lavalink node events and delegates audio management tasks to the appropriate music
 * managers and schedulers. Handles events like track start, track end, and WebSocket disconnection to ensure smooth
 * audio playback.
 *
 * @property musicManagersBean Manages music-related resources and states across multiple guilds.
 * @property jdaInstance Provides access to the JDA instance for guild and voice channel management.
 * @author Miłosz Gilga
 */
@SingletonComponent
class LavaNodeListenerAdapter(
	private val musicManagersBean: MusicManagersBean,
	private val jdaInstance: JdaInstance,
) : LavaNodeListener {

	/**
	 * Triggered when a track starts playing on a Lavalink node. It delegates the event to the audio scheduler to handle
	 * the start of the track.
	 *
	 * @param event The event containing track and node information.
	 */
	override fun onTrackStart(event: TrackStartEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioStart(event.track, event.node)
	}

	/**
	 * Triggered when a track ends playing on a Lavalink node. It delegates the event to the audio scheduler to handle
	 * post-track processing.
	 *
	 * @param event The event containing track, node, and end reason information.
	 */
	override fun onTrackEnd(event: TrackEndEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioEnd(event.track, event.node, event.endReason)
	}

	/**
	 * Triggered when a track gets stuck on a Lavalink node. It delegates the event to the audio scheduler to handle the
	 * stuck track scenario.
	 *
	 * @param event The event containing track and node information.
	 */
	override fun onTrackStuck(event: TrackStuckEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioStuck(event.track, event.node)
	}

	/**
	 * Triggered when an exception occurs during track playback on a Lavalink node. It delegates the event to the audio
	 * scheduler to handle the exception.
	 *
	 * @param event The event containing track, node, and exception information.
	 */
	override fun onTrackException(event: TrackExceptionEvent) {
		val audioScheduler = getAudioScheduler(event.guildId)
		audioScheduler?.onAudioException(event.track, event.node, event.exception)
	}

	/**
	 * Triggered when the WebSocket connection to a Lavalink node is closed. If the closure is due to an invalid session,
	 * attempts to reconnect the bot to the voice channel.
	 *
	 * @param event The event containing details about the WebSocket closure.
	 */
	override fun onCloseWsConnection(event: WebSocketClosedEvent) {
		if (event.code == WS_SESSION_INVALID) {
			val guild = jdaInstance.getGuildById(event.guildId)
			val connectionChannel = guild?.selfMember?.voiceState?.channel ?: return
			jdaInstance.directAudioController.reconnect(connectionChannel)
		}
	}

	/**
	 * Retrieves the audio scheduler for the given guild ID. The scheduler is responsible for managing the audio queue
	 * and playback.
	 *
	 * @param guildId The ID of the guild for which to retrieve the scheduler.
	 * @return The [AudioScheduleHandler] instance or null if no scheduler is available.
	 */
	private fun getAudioScheduler(guildId: Long): AudioScheduleHandler? {
		val musicManager = musicManagersBean.getCachedMusicManager(guildId)
		return musicManager?.state?.audioScheduler
	}
}
