/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio

/**
 * Enum representing the different states of audio processing within the application. Each state indicates a specific
 * mode of operation for handling audio.
 *
 * @author Miłosz Gilga
 */
enum class AudioContentType {

	/**
	 * Indicates that tracks are being queued for playback. This state is typically used when a user requests to add
	 * tracks to the playlist.
	 */
	QUEUE_TRACK,

	/**
	 * Indicates that audio is being streamed live. This state is used when audio is being played from a live source
	 * rather than from a pre-recorded track.
	 */
	STREAM,
	;
}
