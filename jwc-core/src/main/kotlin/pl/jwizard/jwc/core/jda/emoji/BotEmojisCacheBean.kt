/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.emoji

import net.dv8tion.jda.api.JDAInfo
import pl.jwizard.jwac.util.logger
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwc.core.jda.emoji.BotEmoji.entries
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.http.HttpClientFacadeBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.ext.getAsLong
import pl.jwizard.jwl.util.ext.getAsText

/**
 * Manages the cache of custom emojis used by the bot. Provides functionality to load and retrieve bot-specific
 * emojis from the Discord API and store them in memory for efficient access.
 *
 * @property environment Provides access to environment-specific configurations.
 * @property httpClientFacade A utility for making HTTP requests to the Discord API.
 * @author Miłosz Gilga
 */
@SingletonComponent
class BotEmojisCacheBean(
	private val environment: EnvironmentBean,
	private val httpClientFacade: HttpClientFacadeBean,
) {

	companion object {
		private val log = logger<BotEmojisCacheBean>()
	}

	/**
	 * A mutable map that internally stores the emojis fetched from the Discord API. The keys are the emoji display
	 * names, and the values are their corresponding Discord IDs.
	 */
	private val internalEmojis = mutableMapOf<String, Long>()

	/**
	 * Provides a read-only view of the loaded emojis.
	 */
	val emojis
		get() = internalEmojis.toMap()

	/**
	 * Loads the custom emojis available to the bot and populates the internal emoji cache. This method fetches emoji
	 * data from the Discord API and logs the number of successfully loaded and missing emojis.
	 *
	 * @param shardManager Provides bot-specific details, such as the bot's ID.
	 */
	fun loadCustomEmojis(shardManager: JdaShardManagerBean) {
		val persistedEmojis = fetchBotEmojis(shardManager)
		val declaredEmojis = entries.map { it.displayName }
		val notLoadedEmojis = declaredEmojis.subtract(persistedEmojis.keys)
		log.info(
			"Load: {}/{} emojis. Unable to load emojis: {}.",
			persistedEmojis.size,
			declaredEmojis.size,
			notLoadedEmojis
		)
		internalEmojis.putAll(persistedEmojis)
	}

	/**
	 * Fetches custom emojis available to the bot from the Discord API. This method makes an authenticated HTTP request
	 * to retrieve emojis and associates them with their names.
	 *
	 * @param shardManager Provides the bot's ID for API requests.
	 * @return A map where keys are emoji names and values are their respective Discord IDs.
	 */
	private fun fetchBotEmojis(shardManager: JdaShardManagerBean): Map<String, Long> {
		val botId = shardManager.getSelfUserId()

		val secretToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
		val baseUrl = environment.getProperty<String>(BotProperty.LINK_WEBSITE)
		val discordApi = environment.getProperty<String>(BotProperty.SERVICE_DISCORD_API)

		val headers = mapOf(
			"Authorization" to "Bot $secretToken",
			"User-Agent" to "DiscordBot(${baseUrl}, ${JDAInfo.VERSION})",
		)
		val response = httpClientFacade.getJsonListCall("$discordApi/applications/$botId/emojis", headers)
		return response["items"].associate { it.getAsText("name") to it.getAsLong("id") }
	}
}
