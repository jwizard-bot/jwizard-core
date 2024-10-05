/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.radio.spi

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
	 * Retrieves a cached playback message by its class name.
	 *
	 * @param className The name of the class for which the cached playback message is requested.
	 * @return A cached instance of [RadioPlaybackMessage] associated with the specified class name, or null if no such
	 *         instance is found.
	 */
	fun getCachedByClassName(className: String?): RadioPlaybackMessage?
}
