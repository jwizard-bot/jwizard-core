package pl.jwizard.jwc.core.jda.emoji

import net.dv8tion.jda.api.JDAInfo
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwc.core.jda.emoji.BotEmoji.entries
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.http.HttpClientFacadeBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.ext.getAsLong
import pl.jwizard.jwl.util.ext.getAsText
import pl.jwizard.jwl.util.logger

@SingletonComponent
class BotEmojisCacheBean(
	private val environment: EnvironmentBean,
	private val httpClientFacade: HttpClientFacadeBean,
) {
	companion object {
		private val log = logger<BotEmojisCacheBean>()
	}

	private val internalEmojis = mutableMapOf<String, Long>()

	val emojis
		get() = internalEmojis.toMap()

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

	private fun fetchBotEmojis(shardManager: JdaShardManagerBean): Map<String, Long> {
		val botId = shardManager.getSelfUserId()

		val secretToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
		val baseUrl = environment.getProperty<String>(BotProperty.LINK_WEBSITE)
		val discordApi = environment.getProperty<String>(BotProperty.SERVICE_DISCORD_API)

		val headers = mapOf(
			"Authorization" to "Bot $secretToken",
			"User-Agent" to "DiscordBot(${baseUrl}, ${JDAInfo.VERSION})",
		)
		val response = httpClientFacade
			.getJsonListCall("$discordApi/applications/$botId/emojis", headers)
		return response["items"].associate { it.getAsText("name") to it.getAsLong("id") }
	}
}
