/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.settings

import java.io.IOException
import pl.jwizard.core.bot.BotProperties
import pl.jwizard.core.exception.UtilException
import pl.jwizard.core.http.ApiUrl
import pl.jwizard.core.http.HttpClient
import pl.jwizard.core.log.AbstractLoggingBean
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Component
class GuildSettings(
	@Lazy private val httpClient: HttpClient,
	private val botProperties: BotProperties,
) : AbstractLoggingBean(GuildSettings::class) {

	private val guildsData = mutableMapOf<String, GuildSettingsDetails>()

	fun getAndPersistGuildSettings(guildId: String) {
		if (guildsData.contains(guildId)) {
			return // skipping for existing guild
		}
		val request = Request.Builder()
			.url("${ApiUrl.STANDALONE_GUILD_SETTINGS.getUrl(botProperties)}/${guildId}")
			.method(HttpMethod.POST.name(), "".toRequestBody())
			.build()
		try {
			val response = httpClient.makeSecureBlockCall(request)
			val guildSettings = httpClient.mapResponseObject(response, GuildSettingsDetails::class)
				?: throw IOException("Unable map response to settings object")

			guildsData[guildId] = guildSettings
			log.info("Successfully fetch and persisted guild ({}) settings: {}", guildId, guildSettings)
		} catch (ex: IOException) {
			log.error("Unable to fetch/create guid for guild ID: {}. Cause: {}", guildId, ex.message)
		}
	}

	fun removeDefaultMusicTextChannel(guildId: String) {
		val request = Request.Builder()
			.url("${ApiUrl.REMOVE_MUSIC_TEXT_CHANNEL.getUrl(botProperties)}/${guildId}")
			.delete()
			.build()
		try {
			val response = httpClient.makeSecureBlockCall(request)
			if (response.code != 204) {
				throw IOException(response.body?.string())
			}
			log.info("Successfully deleted guild music channel for guild ID: {}", guildId)
		} catch (ex: IOException) {
			log.error("Unable to delete guild music channel for guild ID: {}. Cause: {}", guildId, ex.message)
		}
	}

	fun deleteGuildSettings(guildId: String) {
		val request = Request.Builder()
			.url("${ApiUrl.STANDALONE_GUILD_SETTINGS.getUrl(botProperties)}/${guildId}")
			.delete()
			.build()
		try {
			val response = httpClient.makeSecureBlockCall(request)
			if (response.code != 204) {
				throw IOException(response.body?.string())
			}
			guildsData.remove(guildId)
			log.info("Successfully deleted guild settings for guild ID: {}", guildId)
		} catch (ex: IOException) {
			log.error("Unable to delete guild settings for guild ID: {}. Cause: {}", guildId, ex.message)
		}
	}

	fun getGuildProperties(guildId: String?): GuildSettingsDetails = guildsData[guildId]
		?: throw UtilException.UnexpectedException("Guild properties for guild $guildId not exist")
}
