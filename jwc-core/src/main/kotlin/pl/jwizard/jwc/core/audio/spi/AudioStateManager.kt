/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

import pl.jwizard.jwc.core.audio.AudioContentType

/**
 * Interface for managing the state of audio playback and scheduling in the audio system.
 *
 * The [AudioStateManager] interface provides access to various components responsible for managing audio playback,
 * including audio schedulers for regular tracks and radio streams. It also allows checking the declared content type
 * of the audio being played.
 *
 * @author Miłosz Gilga
 */
interface AudioStateManager {

	/**
	 * Gets the audio scheduler responsible for managing the playback lifecycle.
	 */
	val audioScheduler: AudioScheduler

	/**
	 * Gets the queue track scheduler responsible for managing queued tracks.
	 */
	val queueTrackScheduler: QueueTrackScheduler

	/**
	 * Gets the radio stream scheduler responsible for managing radio stream playback.
	 */
	val radioStreamScheduler: RadioStreamScheduler

	/**
	 * Checks if the given audio content type is declared as part of the audio system.
	 *
	 * This method is used to determine if a specific type of audio content
	 * is supported or currently recognized by the system.
	 *
	 * @param audioType The type of audio content to check.
	 * @return `true` if the audio content type is declared, `false` otherwise.
	 * @see AudioContentType
	 */
	fun isDeclaredAudioContentType(audioType: AudioContentType): Boolean
}
