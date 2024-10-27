/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.manager

import org.springframework.stereotype.Component
import pl.jwizard.jwc.audio.spi.RadioStationThumbnailSupplier
import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.audio.spi.DistributedAudioClientSupplier
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.audio.spi.MusicManagersSupplier
import pl.jwizard.jwc.core.exception.spi.ExceptionTrackerHandler
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.jda.spi.JdaInstance
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.i18n.I18nBean

/**
 * Class responsible for managing music-related functionalities for different guilds. It provides methods for caching
 * and retrieving music managers and handles their lifecycle.
 *
 * @property exceptionTrackerHandler Handles exceptions and logs errors.
 * @property i18nBean Provides internationalization support for the bot.
 * @property jdaColorStoreBean Supplies JDA color configurations for embeds.
 * @property environmentBean Stores environment variables used for configuration.
 * @property eventQueueBean Manages event queue interactions.
 * @property looselyTransportHandlerBean Handles loosely-typed transport operations between services.
 * @property jdaInstance Instance of the JDA client for interacting with Discord.
 * @property radioStationThumbnailSupplier Supplies thumbnails for radio stations.
 * @author Miłosz Gilga
 */
@Component
class MusicManagersBean(
	val exceptionTrackerHandler: ExceptionTrackerHandler,
	val i18nBean: I18nBean,
	val jdaColorStoreBean: JdaColorStoreBean,
	val environmentBean: EnvironmentBean,
	val eventQueueBean: EventQueueBean,
	val looselyTransportHandlerBean: LooselyTransportHandlerBean,
	val jdaInstance: JdaInstance,
	val radioStationThumbnailSupplier: RadioStationThumbnailSupplier,
) : MusicManagersSupplier {

	/**
	 * A map that stores music managers for each guild, indexed by the guild's ID.
	 */
	private val musicManagers = mutableMapOf<Long, GuildMusicManager>()

	/**
	 * Retrieves or creates a new music manager for a guild. If the guild already has a cached music manager, it is
	 * returned. Otherwise, a new one is created and added to the cache.
	 *
	 * @param context The command context containing guild and user information.
	 * @param future The future response object used to send interaction responses.
	 * @param distributedAudioClientSupplier Supplier for the distributed audio client to handle playback.
	 * @return A music manager associated with the guild.
	 */
	override fun getOrCreateMusicManager(
		context: CommandBaseContext,
		future: TFutureResponse,
		distributedAudioClientSupplier: DistributedAudioClientSupplier,
	): MusicManager = synchronized(this) {
		val manager = musicManagers.getOrPut(context.guild.idLong) {
			GuildMusicManager(this, context, future, distributedAudioClientSupplier)
		}
		manager.state.updateFutureResponse(future)
		manager
	}

	/**
	 * Retrieves the cached music manager for a given guild by its ID.
	 *
	 * @param guildId The ID of the guild whose music manager should be retrieved.
	 * @return The music manager associated with the specified guild, or null if not found.
	 */
	fun getCachedMusicManager(guildId: Long) = musicManagers[guildId]

	/**
	 * Removes the music manager for a given guild by its ID and disposes of its resources.
	 *
	 * @param guildId The ID of the guild whose music manager should be removed.
	 */
	fun removeMusicManager(guildId: Long) = musicManagers.remove(guildId)?.dispose()
}
