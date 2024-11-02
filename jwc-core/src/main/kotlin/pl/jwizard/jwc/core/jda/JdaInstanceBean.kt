/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Icon
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.managers.AccountManager
import net.dv8tion.jda.api.managers.DirectAudioController
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.internal.managers.AccountManagerImpl
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.audio.spi.DistributedAudioClientSupplier
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.jda.spi.JdaInstance
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.SpringKtContextFactory
import pl.jwizard.jwl.jvm.JvmDisposable
import pl.jwizard.jwl.jvm.JvmDisposableHook
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.util.logger

/**
 * Manages the JDA (Java Discord API) client instance.
 *
 * This class is responsible for initializing and configuring the JDA client used to interact with the Discord API.
 * It sets up various configurations such as cache settings, gateway intents, activity status, and permissions.
 *
 * @property environmentBean Provides access to application properties, including the bot token.
 * @property applicationContext The Spring context factory used to load and retrieve components.
 * @property jdaColorStoreBean Provides access to JDA colors loader.
 * @author Miłosz Gilga
 */
@Component
final class JdaInstanceBean(
	private val environmentBean: EnvironmentBean,
	private val applicationContext: SpringKtContextFactory,
	private val jdaColorStoreBean: JdaColorStoreBean,
) : JdaInstance, JvmDisposable {

	companion object {
		private val log = logger<JdaInstanceBean>()
	}

	/**
	 * The JDA (Java Discord API) client instance. Initializing in [createJdaWrapper] method.
	 */
	private final lateinit var jda: JDA

	/**
	 * Instance of [JvmDisposableHook] responsible for managing JVM shutdown hooks.
	 */
	private val jvmDisposableHook = JvmDisposableHook(this)

	/**
	 * Initializes and configures a JDA (Java Discord API) client.
	 *
	 * This method creates and configures the JDA client with the specified bot token and gateway intents,
	 * sets cache settings, activity status, and adds event listeners (to be defined). It also logs the
	 * initialization progress and provides an invitation URL for adding the bot to a Discord server once ready.
	 *
	 * @param audioClientSupplier Provides access to the distributed client for audio streaming functionalities.
	 * @throws InterruptedException If waiting for the JDA client to be ready is interrupted.
	 * @throws InvalidTokenException If there is an issue with the bot token or login process.
	 */
	fun createJdaWrapper(audioClientSupplier: DistributedAudioClientSupplier) {
		log.info("JDA instance is warming up...")
		jdaColorStoreBean.loadColors()

		val gatewayIntents = environmentBean.getListProperty<String>(BotListProperty.JDA_GATEWAY_INTENTS)
		val enabledCacheFlags = environmentBean.getListProperty<String>(BotListProperty.JDA_CACHE_FLAGS_ENABLED)
		val disabledCacheFlags = environmentBean.getListProperty<String>(BotListProperty.JDA_CACHE_FLAGS_DISABLED)
		val permissionFlags = environmentBean.getListProperty<String>(AppBaseListProperty.JDA_PERMISSIONS)

		val permissions = permissionFlags.map { Permission.valueOf(it) }
		log.info("Load: {} JDA permissions.", permissions.size)

		val eventListeners = applicationContext.getBeansAnnotatedWith<EventListener, JdaEventListenerBean>()
		log.info("Load: {} JDA event listeners: {}.", eventListeners.size, eventListeners.map { it.javaClass.simpleName })

		val jdaToken = environmentBean.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)

		log.info("Load: {} gateway intents: {}.", gatewayIntents.size, gatewayIntents)
		log.info("Load: {} enabled cache flags: {}.", enabledCacheFlags.size, enabledCacheFlags)
		log.info("Load: {} disabled cache flags: {}.", disabledCacheFlags.size, disabledCacheFlags)

		jda = JDABuilder
			.create(jdaToken, gatewayIntents.map { GatewayIntent.valueOf(it) })
			.setVoiceDispatchInterceptor(audioClientSupplier.voiceDispatchInterceptor)
			.enableCache(enabledCacheFlags.map { CacheFlag.valueOf(it) })
			.disableCache(disabledCacheFlags.map { CacheFlag.valueOf(it) })
			.setActivity(Activity.listening("Loading..."))
			.setStatus(OnlineStatus.ONLINE)
			.addEventListeners(*eventListeners.toTypedArray())
			.setBulkDeleteSplittingEnabled(true)
			.build()
			.awaitReady()

		jvmDisposableHook.initHook()
		log.info("Add bot into Discord server via link: {}", jda.getInviteUrl(permissions))
	}

	/**
	 * Cleans up resources before JVM shutdown. This method shuts down the JDA client and logs the state transition
	 * of the JDA instance.
	 */
	override fun cleanBeforeDisposeJvm() {
		val previousState = jda.status
		jda.shutdownNow()
		log.info("JDA instance change state from: {} to: {}.", previousState, jda.status)
	}

	/**
	 * Sets the presence activity of the JDA instance to a listening activity with the specified [activity] description.
	 *
	 * @param activity The activity description to set for the presence.
	 */
	override fun setPresenceActivity(activity: String) {
		jda.presence.activity = Activity.listening(activity)
	}

	/**
	 * Retrieves the [Guild] object associated with the specified [guildId].
	 *
	 * @param guildId The ID of the guild to retrieve.
	 * @return The [Guild] object associated with the given ID, or `null` if no such guild is found.
	 */
	override fun getGuildById(guildId: Long) = jda.getGuildById(guildId)

	/**
	 * Retrieves a [User] object associated with the specified userId.
	 *
	 * This method allows for the retrieval of a user object from the JDA instance using the provided user ID.
	 * It can be used for various actions, such as sending messages or managing user-related features.
	 *
	 * @param userId The ID of the user to retrieve.
	 * @return The [User] object associated with the given ID, or `null` if no such user is found.
	 */
	override fun getUserById(userId: Long) = jda.getUserById(userId)

	/**
	 * Provides access to the [DirectAudioController] for managing audio playback in voice channels.
	 *
	 * This property allows interaction with the audio functionalities of the JDA instance, enabling the bot to play
	 * audio in voice channels and manage audio-related tasks.
	 */
	override val directAudioController
		get() = jda.directAudioController
}
