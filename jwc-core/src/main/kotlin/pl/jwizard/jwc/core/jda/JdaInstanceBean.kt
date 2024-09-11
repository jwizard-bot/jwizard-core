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
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.managers.AccountManager
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.internal.managers.AccountManagerImpl
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.spi.JdaInstance
import pl.jwizard.jwc.core.jda.spi.JdaPermissionFlagsSupplier
import pl.jwizard.jwc.core.jvm.JvmDisposable
import pl.jwizard.jwc.core.jvm.JvmDisposableHook
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.s3.S3ClientBean
import pl.jwizard.jwc.core.s3.S3Object

/**
 * Manages the JDA (Java Discord API) client instance.
 *
 * This class is responsible for initializing and configuring the JDA client used to interact with the Discord API.
 * It sets up various configurations such as cache settings, gateway intents, activity status, and permissions.
 * The class also provides methods to configure additional metadata and gracefully shut down the JDA client.
 *
 * @property environmentBean Provides access to application properties, including the bot token.
 * @property jdaPermissionFlagsSupplier Provides bean supplied JDA permission flags.
 * @property s3ClientBean S3 client bean available get interaction with S3 API.
 * @constructor Creates an instance of [JdaInstanceBean] with the specified [environmentBean].
 * @author Miłosz Gilga
 */
@Component
final class JdaInstanceBean(
	private val environmentBean: EnvironmentBean,
	private val jdaPermissionFlagsSupplier: JdaPermissionFlagsSupplier,
	private val s3ClientBean: S3ClientBean,
) : JdaInstance, JvmDisposable {

	companion object {
		private val log = LoggerFactory.getLogger(JdaInstanceBean::class.java)

		/**
		 * A list of [GatewayIntent] instances that are enabled for the application.
		 */
		private val GATEWAY_INTENTS = arrayListOf(
			GatewayIntent.DIRECT_MESSAGES,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.GUILD_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_VOICE_STATES,
			GatewayIntent.GUILD_MESSAGE_TYPING,
			GatewayIntent.GUILD_MEMBERS,
		)

		/**
		 * A list of [CacheFlag] instances that are enabled for caching.
		 */
		private val ENABLED_CACHE_FLAGS = arrayListOf(
			CacheFlag.MEMBER_OVERRIDES,
			CacheFlag.VOICE_STATE,
		)

		/**
		 * A list of [CacheFlag] instances that are disabled for caching.
		 */
		private val DISABLED_CACHE_FLAGS = arrayListOf(
			CacheFlag.ACTIVITY,
			CacheFlag.CLIENT_STATUS,
			CacheFlag.EMOJI,
			CacheFlag.ONLINE_STATUS,
			CacheFlag.SCHEDULED_EVENTS,
			CacheFlag.STICKER,
		)
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
	 * @throws InterruptedException If waiting for the JDA client to be ready is interrupted.
	 * @throws InvalidTokenException If there is an issue with the bot token or login process.
	 */
	fun createJdaWrapper() {
		log.info("JDA instance is warming up...")

		val permissionFlags = jdaPermissionFlagsSupplier.getPermissionFlags()
		val permissions = mutableListOf<Permission>()

		for (flag in permissionFlags) {
			permissions.add(Permission.valueOf(flag))
		}
		log.info("Load: {} JDA permissions.", permissions.size)

		jda = JDABuilder
			.create(environmentBean.getProperty(BotProperty.JDA_SECRET_TOKEN), GATEWAY_INTENTS)
			.enableCache(ENABLED_CACHE_FLAGS)
			.disableCache(DISABLED_CACHE_FLAGS)
			.setActivity(Activity.listening("Loading..."))
			.setStatus(OnlineStatus.ONLINE)
			.addEventListeners(/*TODO*/)
			.setBulkDeleteSplittingEnabled(true)
			.build()
			.awaitReady()

		jvmDisposableHook.initHook()
		log.info("Add bot into Discord server via link: {}", jda.getInviteUrl(permissions))
	}

	/**
	 * Configures the metadata for the bot account, such as its name, avatar, and banner.
	 *
	 * This method retrieves the logo and banner images from S3, sets them as the bot's avatar and banner using the
	 * [AccountManager], and updates the bot's name. The method also logs the metadata properties that were configured.
	 *
	 * It fetches the `LOGO` and `BANNER` resources from S3, converts them into input streams, and uses them to set the
	 * avatar and banner for the bot. If the resources are successfully retrieved, they are logged along with the
	 * bot's name.
	 *
	 * @throws IllegalArgumentException If the name property is not present in the environment configuration.
	 * @see S3ClientBean.getPublicObject
	 */
	fun configureMetadata() {
		val logoInputStream = s3ClientBean.getPublicObject(S3Object.LOGO)
		val bannerInputStream = s3ClientBean.getPublicObject(S3Object.BANNER)

		val accountManager = AccountManagerImpl(jda.selfUser)
		val metadataProperties = mutableListOf<String>()

		val name = environmentBean.getProperty<String>(BotProperty.JDA_NAME)
		accountManager.setName(name)
		metadataProperties.add("name: $name")

		logoInputStream?.use {
			accountManager.setAvatar(Icon.from(it))
			metadataProperties.add("avatar: ${S3Object.LOGO.resourcePath}")
		}
		bannerInputStream?.use {
			accountManager.setBanner(Icon.from(it))
			metadataProperties.add("banner: ${S3Object.BANNER.resourcePath}")
		}
		log.info("Configure metadata properties: {}.", metadataProperties)
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
	override fun getGuildById(guildId: String) = jda.getGuildById(guildId)
}
