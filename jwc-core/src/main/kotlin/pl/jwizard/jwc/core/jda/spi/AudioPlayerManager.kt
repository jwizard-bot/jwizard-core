/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

/**
 * Interface for managing audio player sources in the application.
 *
 * This interface defines a method for registering various audio source managers with the audio player. Implementations
 * of this interface should handle the configuration and setup of audio sources to ensure proper playback functionality.
 *
 * @author Miłosz Gilga
 */
interface AudioPlayerManager {

	/**
	 * Registers all audio source managers with the audio player.
	 *
	 * This method should be implemented to register different audio sources (e.g., YouTube, SoundCloud, Bandcamp) with
	 * the audio player manager. It should also configure settings related to HTTP requests and pagination if applicable.
	 * Implementations should ensure that all required source managers are properly registered and configured for audio
	 * playback.
	 */
	fun registerSources()
}
