/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwl.IoCKtContextFactory
import pl.jwizard.jwl.util.logger
import java.util.concurrent.ConcurrentHashMap

/**
 * This class implements the caching mechanism for radio playback mapper components. It loads and stores components
 * annotated with [RadioPlaybackMapper] and provides a way to retrieve them by class name from the cache.
 *
 * @property ioCKtContextFactory Provides access to the IoC context for retrieving beans.
 * @author Miłosz Gilga
 */
@Component
class RadioPlaybackMappersCacheBean(
	private val ioCKtContextFactory: IoCKtContextFactory,
) : RadioPlaybackMappersCache {

	companion object {
		private val log = logger<RadioPlaybackMappersCacheBean>()
	}

	/**
	 * Cache for storing radio playback mapper components, mapped by their class names.
	 */
	private val playbackComponents = ConcurrentHashMap<String, RadioPlaybackMapperHandler>()

	/**
	 * Loads all beans annotated with [RadioPlaybackMapper] from the Spring application context and caches them using
	 * their simple class names as keys.
	 */
	override fun loadRadioPlaybackClasses() {
		val components = ioCKtContextFactory.getBeansAnnotatedWith<RadioPlaybackMapperHandler, RadioPlaybackMapper>()
		components.forEach { playbackComponents[it.javaClass.simpleName] = it }
		log.info("Load: {} radio playback components from Spring Context.", components.size)
	}

	/**
	 * Retrieves a cached [RadioPlaybackMapperHandler] component by its class name.
	 *
	 * @param className The name of the class used as a key in the cache.
	 * @return The corresponding [RadioPlaybackMapperHandler], or null if not found.
	 */
	override fun getCachedByClassName(className: String?) = className?.let { playbackComponents[it] }
}
