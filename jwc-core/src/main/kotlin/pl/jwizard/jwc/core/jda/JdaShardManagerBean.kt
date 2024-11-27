/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda

import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.managers.DirectAudioController
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import pl.jwizard.jwc.core.audio.spi.DistributedAudioClient
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.jvm.JvmDisposable
import pl.jwizard.jwl.jvm.JvmDisposableHook
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.util.logger

/**
 * Manages the initialization, lifecycle, and interaction with the JDA (Java Discord API) instance for a bot.
 * This includes managing the shards, setting bot presence, and handling events for guilds and users.
 *
 * @property environment Provides access to application properties, including the bot token.
 * @property jdaColorStore Provides access to JDA colors loader.
 * @property ioCKtContextFactory Provides access to the IoC context for retrieving beans.
 * @author Miłosz Gilga
 */
@SingletonComponent
final class JdaShardManagerBean(
	private val environment: EnvironmentBean,
	private val jdaColorStore: JdaColorsCacheBean,
	private val ioCKtContextFactory: IoCKtContextFactory,
) : JvmDisposable {

	companion object {
		private val log = logger<JdaShardManagerBean>()
	}

	/**
	 * The ShardManager instance responsible for managing all the JDA shards.
	 */
	private final lateinit var shardManager: ShardManager

	/**
	 * Instance of [JvmDisposableHook] responsible for managing JVM shutdown hooks.
	 */
	private val jvmDisposableHook = JvmDisposableHook(this)

	/**
	 * Creates and initializes the JDA shard manager, responsible for managing the bot's connection to Discord.
	 *
	 * This method configures the JDA instance, sets the bot’s token, permissions, and gateway intents, initializes event
	 * listeners, and handles audio functionality.
	 *
	 * @param distributedAudioClientSupplier Provides access to the distributed client for audio streaming functionalities.
	 * @throws InterruptedException If waiting for the JDA client to be ready is interrupted.
	 * @throws InvalidTokenException If there is an issue with the bot token or login process.
	 */
	fun createShardsManager(distributedAudioClientSupplier: DistributedAudioClient) {
		log.info("JDA instance is warming up...")
		jdaColorStore.loadColors()

		val gatewayIntents = environment.getListProperty<String>(BotListProperty.JDA_GATEWAY_INTENTS)
		val enabledCacheFlags = environment.getListProperty<String>(BotListProperty.JDA_CACHE_FLAGS_ENABLED)
		val disabledCacheFlags = environment.getListProperty<String>(BotListProperty.JDA_CACHE_FLAGS_DISABLED)
		val permissionFlags = environment.getListProperty<String>(AppBaseListProperty.JDA_PERMISSIONS)

		val permissions = permissionFlags.map { Permission.valueOf(it) }
		log.info("Load: {} JDA permissions.", permissions.size)

		val eventListeners = ioCKtContextFactory.getBeansAnnotatedWith<EventListener, JdaEventListenerBean>()
		log.info("Load: {} JDA event listeners: {}.", eventListeners.size, eventListeners.map { it.javaClass.simpleName })

		val jdaToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)

		log.info("Load: {} gateway intents: {}.", gatewayIntents.size, gatewayIntents)
		log.info("Load: {} enabled cache flags: {}.", enabledCacheFlags.size, enabledCacheFlags)
		log.info("Load: {} disabled cache flags: {}.", disabledCacheFlags.size, disabledCacheFlags)

		val shardingMinId = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_START)
		val shardingMaxId = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_END)
		val shardsCount = (shardingMaxId - shardingMinId) + 1

		shardManager = DefaultShardManagerBuilder
			.create(jdaToken, gatewayIntents.map { GatewayIntent.valueOf(it) })
			.setUseShutdownNow(true)
			.setShardsTotal(shardsCount)
			.setShards(shardingMinId, shardingMaxId)
			.setVoiceDispatchInterceptor(distributedAudioClientSupplier.voiceDispatchInterceptor)
			.enableCache(enabledCacheFlags.map { CacheFlag.valueOf(it) })
			.disableCache(disabledCacheFlags.map { CacheFlag.valueOf(it) })
			.setActivity(Activity.listening("Loading..."))
			.setStatus(OnlineStatus.ONLINE)
			.addEventListeners(*eventListeners.toTypedArray())
			.setBulkDeleteSplittingEnabled(true)
			.build()

		jvmDisposableHook.initHook()

		val clusterName = environment.getProperty<String>(BotProperty.JDA_SHARDING_CLUSTER)
		log.info("Init: {} shards for cluster: {}.", shardsCount, clusterName)
		log.info("Init shards manager from shard id: {} to: {}.", shardingMinId, shardingMaxId)

		log.info("Add bot into Discord server via link: {}", shardManager.getShardById(0)?.getInviteUrl(permissions))
	}

	/**
	 * Cleans up resources before JVM shutdown. This method shuts down the JDA clients from all shards and logs the state
	 * transition of the JDA instance.
	 */
	override fun cleanBeforeDisposeJvm() {
		val shardsCount = shardManager.shards.size
		shardManager.shutdown()
		log.info("JDA instances in: {} shards change was disposed.", shardsCount)
	}

	/**
	 * Sets the presence activity of the JDA instance to a listening activity with the specified [activity] description.
	 *
	 * @param activity The activity description to set for the presence.
	 */
	fun setPresenceActivity(activity: String) {
		shardManager.shards.forEach { it.presence.activity = Activity.listening(activity) }
	}

	/**
	 * Retrieves the [Guild] object associated with the specified [guildId].
	 *
	 * @param guildId The ID of the guild to retrieve.
	 * @return The [Guild] object associated with the given ID, or `null` if no such guild is found.
	 */
	fun getGuildById(guildId: Long) = shardManager.getGuildById(guildId)

	/**
	 * Retrieves a [User] object associated with the specified userId.
	 *
	 * This method allows for the retrieval of a user object from the JDA instance using the provided user ID.
	 * It can be used for various actions, such as sending messages or managing user-related features.
	 *
	 * @param userId The ID of the user to retrieve.
	 * @return The [User] object associated with the given ID, or `null` if no such user is found.
	 */
	fun getUserById(userId: Long) = shardManager.getUserById(userId)

	/**
	 * Retrieves the [DirectAudioController] for the specified guild.
	 *
	 * This controller is used for managing audio functionality in voice channels within a guild.
	 *
	 * @param guild The guild for which the audio controller is requested.
	 * @return The [DirectAudioController] for the specified guild, or `null` if the controller is not available.
	 */
	fun getDirectAudioController(guild: Guild) =
		shardManager.getShardById(guild.jda.shardInfo.shardId)?.directAudioController

	/**
	 * Retrieves detailed statistics about the bot's Discord shards, including the number of running and queued shards,
	 * and the average gateway ping.
	 *
	 * @return A [ShardStatsDetails] instance containing shard statistics.
	 */
	fun getShardDetails() = ShardStatsDetails(
		shardManager.shardsRunning,
		shardManager.shardsQueued,
		shardManager.averageGatewayPing,
	)
}
