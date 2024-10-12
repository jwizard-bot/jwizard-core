/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.lava

import dev.arbjerg.lavalink.client.event.*

/**
 * Interface defining event listeners for Lavalink node-related events. These events provide callbacks for key actions
 * such as track start, track end, connection issues, and exceptions during track playback.
 *
 * @author Miłosz Gilga
 */
interface LavaNodeListener {

	/**
	 * Called when a track starts playing on a Lavalink node.
	 *
	 * @param event The event containing information about the started track.
	 */
	fun onTrackStart(event: TrackStartEvent)

	/**
	 * Called when a track ends playing on a Lavalink node.
	 *
	 * @param event The event containing information about the finished track, including the reason.
	 */
	fun onTrackEnd(event: TrackEndEvent)

	/**
	 * Called when a track gets stuck due to a playback issue on a Lavalink node.
	 *
	 * @param event The event containing information about the track that got stuck.
	 */
	fun onTrackStuck(event: TrackStuckEvent)

	/**
	 * Called when an exception occurs while playing a track on a Lavalink node.
	 *
	 * @param event The event containing details about the exception encountered.
	 */
	fun onTrackException(event: TrackExceptionEvent)

	/**
	 * Called when the WebSocket connection to a Lavalink node is closed.
	 *
	 * @param event The event containing details about the WebSocket closure, such as the close code and reason.
	 */
	fun onCloseWsConnection(event: WebSocketClosedEvent)
}
