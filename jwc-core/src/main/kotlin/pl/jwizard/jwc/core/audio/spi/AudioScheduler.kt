/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

/**
 * Interface for managing audio scheduling in an audio player.
 *
 * The [AudioScheduler] interface provides a contract for scheduling audio playback and handling its lifecycle.
 * Implementing classes should define how audio playback is started, stopped, and managed.
 *
 * @author Miłosz Gilga
 */
interface AudioScheduler {

	/**
	 * Stops the current audio playback and releases any resources associated with it.
	 *
	 * This method should be called when the audio playback is no longer needed, ensuring that all resources are properly
	 * cleaned up to prevent memory leaks.
	 */
	fun stopAndDestroy()
}
