/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.manager

import pl.jwizard.jwc.audio.lava.LavalinkClientBean
import pl.jwizard.jwc.audio.spi.RadioStationThumbnailSupplier
import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.exception.ExceptionTrackerHandlerBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * Class responsible for managing music-related functionalities for different guilds. It provides methods for caching
 * and retrieving music managers and handles their lifecycle.
 *
 * @property exceptionTrackerHandler Handles exceptions and logs errors.
 * @property i18n Provides internationalization support for the bot.
 * @property jdaColorStore Supplies JDA color configurations for embeds.
 * @property environment Stores environment variables used for configuration.
 * @property eventQueue Manages event queue interactions.
 * @property looselyTransportHandler Handles loosely-typed transport operations between services.
 * @property jdaShardManager Manages multiple shards of the JDA bot, responsible for handling Discord API interactions.
 * @property radioStationThumbnailSupplier Supplies thumbnails for radio stations.
 * @author Miłosz Gilga
 */
@SingletonComponent
class MusicManagersBean(
	val exceptionTrackerHandler: ExceptionTrackerHandlerBean,
	val i18n: I18nBean,
	val jdaColorStore: JdaColorStoreBean,
	val environment: EnvironmentBean,
	val eventQueue: EventQueueBean,
	val looselyTransportHandler: LooselyTransportHandlerBean,
	val jdaShardManager: JdaShardManagerBean,
	val radioStationThumbnailSupplier: RadioStationThumbnailSupplier,
) {

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
	 * @param audioClient Supplier for the distributed audio client to handle playback.
	 * @return A music manager associated with the guild.
	 */
	fun getOrCreateMusicManager(
		context: CommandBaseContext,
		future: TFutureResponse,
		audioClient: LavalinkClientBean,
	) = synchronized(this) {
		val manager = musicManagers.getOrPut(context.guild.idLong) {
			GuildMusicManager(this, context, future, audioClient)
		}
		manager.state.updateStateHandlers(future, context)
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
