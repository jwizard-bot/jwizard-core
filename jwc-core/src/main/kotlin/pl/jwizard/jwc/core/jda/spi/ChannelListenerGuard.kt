/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

/**
 * Interface for components that need to listen to voice channel updates in guilds and perform specific actions based
 * on those updates.
 *
 * This interface defines methods for initializing the thread pool and handling voice update events.
 *
 * @author Miłosz Gilga
 */
interface ChannelListenerGuard {

	/**
	 * Initializes the thread pool or any necessary resources for handling voice updates.
	 *
	 * Implementations should start the appropriate executor services or setup required for periodic checks or event
	 * handling.
	 */
	fun initThreadPool()
}
