/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler

import dev.arbjerg.lavalink.client.LavalinkNode
import dev.arbjerg.lavalink.client.player.Track
import dev.arbjerg.lavalink.client.player.TrackException
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason

/**
 * Interface for managing audio job scheduling in a music player context.
 *
 * This interface defines methods for handling different audio events such as loading tracks, starting audio playback,
 * and dealing with audio end, stuck, or exception scenarios.
 *
 * @author Miłosz Gilga
 */
interface AudioJobScheduler {

	/**
	 * Loads a list of audio tracks into the scheduler.
	 *
	 * This method should be called when new tracks are to be queued or played.
	 *
	 * @param tracks The list of tracks to be loaded into the scheduler.
	 */
	fun loadContent(tracks: List<Track>)

	/**
	 * Handles the event when an audio track starts playing.
	 *
	 * This method should be called when a track begins playback, allowing the scheduler to perform any necessary actions,
	 * such as updating the UI or tracking playback statistics.
	 *
	 * @param track The track that has started playing.
	 * @param node The Lavalink node responsible for handling the track.
	 */
	fun onAudioStart(track: Track, node: LavalinkNode)

	/**
	 * Handles the event when an audio track ends.
	 *
	 * This method should be invoked when a track finishes playing, allowing the scheduler to handle any cleanup or
	 * transition to the next track in the queue. It also provides the reason for the end.
	 *
	 * @param lastTrack The track that has just finished playing.
	 * @param node The Lavalink node that handled the playback of the track.
	 * @param endReason The reason why the track ended, such as completion or being skipped.
	 */
	fun onAudioEnd(lastTrack: Track, node: LavalinkNode, endReason: AudioTrackEndReason)

	/**
	 * Handles the event when an audio track gets stuck during playback.
	 *
	 * This method should be called when the track playback is halted unexpectedly, which could indicate an issue with
	 * the audio source or network.
	 *
	 * @param track The track that has become stuck.
	 * @param node The Lavalink node responsible for the stuck playback.
	 */
	fun onAudioStuck(track: Track, node: LavalinkNode)

	/**
	 * Handles exceptions that occur during audio playback.
	 *
	 * This method should be called when an exception is thrown while trying to play a track, allowing the scheduler to
	 * take appropriate actions such as logging the error or notifying the user.
	 *
	 * @param track The track that caused the exception.
	 * @param node The Lavalink node that was handling the track.
	 * @param exception The exception that was thrown during playback.
	 */
	fun onAudioException(track: Track, node: LavalinkNode, exception: TrackException)
}
