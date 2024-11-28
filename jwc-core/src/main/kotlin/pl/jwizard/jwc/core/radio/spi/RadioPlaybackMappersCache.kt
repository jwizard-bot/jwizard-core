/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.radio.spi

import pl.jwizard.jwl.radio.PlaybackProvider

/**
 * Interface for managing the caching of radio playback mappers.
 *
 * This interface provides methods to load and retrieve playback mappers used for processing radio data. Implementing
 * classes should provide functionality to manage the lifecycle and caching of these mappers.
 *
 * @author Miłosz Gilga
 */
interface RadioPlaybackMappersCache {

	/**
	 * Loads the radio playback classes into the cache. This method is responsible for initializing and storing available
	 * playback mapper classes.
	 */
	fun loadRadioPlaybackClasses()

	/**
	 * Retrieves a cached playback mapper for a specified [PlaybackProvider].
	 *
	 * @param provider The [PlaybackProvider] key to use for looking up the playback mapper.
	 * @return The [RadioPlaybackMessage] instance associated with the specified provider, or `null` if no mapper is
	 *         cached for the provider.
	 */
	fun getCachedByProvider(provider: PlaybackProvider?): RadioPlaybackMessage?
}
