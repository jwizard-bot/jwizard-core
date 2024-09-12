/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent

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

	/**
	 * Handles voice update events received from the JDA API.
	 *
	 * This method should be implemented to perform actions when there is a change in the voice state of users in a
	 * guild. For example, it can be used to track whether voice channels become empty and take appropriate actions.
	 *
	 * @param event The [GuildVoiceUpdateEvent] containing information about the voice state update.
	 */
	fun onEveryVoiceUpdate(event: GuildVoiceUpdateEvent)
}
