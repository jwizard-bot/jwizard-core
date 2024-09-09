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
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.stereotype.JdaInstance
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * Manages the JDA (Java Discord API) client instance.
 *
 * This class is responsible for initializing and configuring the JDA client used to interact with the Discord API.
 * It sets up various configurations such as cache settings, gateway intents, activity status, and permissions.
 * The class also provides methods to configure additional metadata and gracefully shut down the JDA client.
 *
 * @property environmentBean Provides access to application properties, including the bot token.
 * @constructor Creates an instance of [JdaInstanceBean] with the specified [environmentBean].
 *
 * @author Miłosz Gilga
 */
@Component
class JdaInstanceBean(private val environmentBean: EnvironmentBean) : JdaInstance {

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
		 * A list of [Permission] instances required for the application to function properly.
		 */
		private val PERMISSIONS = arrayListOf(
			Permission.VIEW_CHANNEL,
			Permission.MESSAGE_SEND,
			Permission.MESSAGE_HISTORY,
			Permission.MESSAGE_ADD_REACTION,
			Permission.MESSAGE_EMBED_LINKS,
			Permission.MESSAGE_ATTACH_FILES,
			Permission.MESSAGE_MANAGE,
			Permission.MESSAGE_EXT_EMOJI,
			Permission.MANAGE_CHANNEL,
			Permission.VOICE_CONNECT,
			Permission.VOICE_SPEAK,
			Permission.USE_APPLICATION_COMMANDS,
			Permission.MANAGE_ROLES,
			Permission.VOICE_DEAF_OTHERS,
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

		log.info("Add bot into Discord server via link: {}", jda.getInviteUrl(PERMISSIONS))
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
	 * TODO
	 *
	 */
	fun gracefullyShutdown() {
		log.info("shutdown JDA")
	}
}
