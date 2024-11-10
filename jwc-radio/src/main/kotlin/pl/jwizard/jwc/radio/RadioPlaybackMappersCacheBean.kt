/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio

import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.ioc.reflect.ClasspathScanner
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.radio.PlaybackProvider
import pl.jwizard.jwl.util.logger
import java.util.concurrent.ConcurrentHashMap

/**
 * This class implements the caching mechanism for radio playback mapper components. It loads and stores components
 * annotated with [RadioPlaybackMapper] and provides a way to retrieve them by class name from the cache.
 *
 * @property ioCKtContextFactory Provides access to the IoC context for retrieving beans.
 * @author Miłosz Gilga
 */
@SingletonComponent
class RadioPlaybackMappersCacheBean(
	private val ioCKtContextFactory: IoCKtContextFactory,
) : RadioPlaybackMappersCache {

	companion object {
		private val log = logger<RadioPlaybackMappersCacheBean>()

		/**
		 * Subpackage used for scanning provider classes.
		 */
		private const val SCANNING_SUBPACKAGE = "jwc.radio.mapper"
	}

	/**
	 * Cache for storing radio playback mapper components, mapped by their class names.
	 */
	private val playbackComponents = ConcurrentHashMap<PlaybackProvider, RadioPlaybackMapperHandler>()

	/**
	 * Scans the classpath to find classes annotated with [RadioPlaybackMapper] in the specified subpackage.
	 */
	private val scanner = ClasspathScanner(RadioPlaybackMapper::class, SCANNING_SUBPACKAGE)

	/**
	 * Loads all beans annotated with [RadioPlaybackMapper] from the IoC application context and caches them using
	 * their simple class names as keys.
	 */
	override fun loadRadioPlaybackClasses() {
		scanner.findComponents().forEach { (provider, clazz) ->
			playbackComponents[provider.value] = ioCKtContextFactory.getBean(clazz) as RadioPlaybackMapperHandler
		}
		log.info("Load: {} radio playback components from IoC context.", playbackComponents.size)
	}

	/**
	 * Retrieves a cached [RadioPlaybackMapperHandler] by its associated [PlaybackProvider].
	 *
	 * @param provider The [PlaybackProvider] key to search in the cache.
	 * @return The cached [RadioPlaybackMapperHandler] instance for the given provider, or `null` if not found.
	 */
	override fun getCachedByProvider(provider: PlaybackProvider?) = provider?.let { playbackComponents[it] }
}
