package pl.jwizard.jwc.core.jda.emoji

import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.JDAInfo
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.JdaShardManager
import pl.jwizard.jwc.core.jda.emoji.BotEmoji.entries
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.util.ext.getAsLong
import pl.jwizard.jwl.util.ext.getAsText
import pl.jwizard.jwl.util.logger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class BotEmojisCache(
	environment: BaseEnvironment,
	private val objectMapper: ObjectMapper,
	private val httpClient: HttpClient,
) {
	companion object {
		private val log = logger<BotEmojisCache>()
	}

	private val secretToken = environment.getProperty<String>(BotProperty.JDA_SECRET_TOKEN)
	private val baseUrl = environment.getProperty<String>(BotProperty.LINK_WEBSITE)
	private val discordApi = environment.getProperty<String>(BotProperty.SERVICE_DISCORD_API)

	private val internalEmojis = mutableMapOf<String, Long>()

	val emojis
		get() = internalEmojis.toMap()

	fun loadCustomEmojis(shardManager: JdaShardManager) {
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

	private fun fetchBotEmojis(shardManager: JdaShardManager): Map<String, Long> {
		val botId = shardManager.getSelfUserId()

		val httpRequest = HttpRequest.newBuilder()
			.uri(URI.create("$discordApi/applications/$botId/emojis"))
			.header("Authorization", "Bot $secretToken")
			.header("User-Agent", "DiscordBot(${baseUrl}, ${JDAInfo.VERSION})")
			.build()
		val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
		if (response.statusCode() != 200) {
			throw RuntimeException(
				"Could not perform call: ${response.uri()}. Ended code: ${response.statusCode()}.",
			)
		}
		val responseData = objectMapper.readTree(response.body())
		return responseData["items"].associate { it.getAsText("name") to it.getAsLong("id") }
	}
}
