/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.cdn

import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.bot.properties.BotProperties
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

enum class CdnResource(
	private val dirName: String
) {
	BRAND("logo/brand"),
	RADIO_STATIONS("logo/radio-station"),
	;

	fun getResourceUrl(botProperties: BotProperties, fileName: String): String = StringJoiner("/")
		.add(botProperties.cdn.host)
		.add(dirName)
		.add(fileName)
		.toString()

	fun getResourceUrl(botConfiguration: BotConfiguration, fileName: String): String =
		getResourceUrl(botConfiguration.botProperties, fileName)

	fun getAndDownloadResource(botProperties: BotProperties, fileName: String): InputStream? {
		val client = HttpClient.newHttpClient()
		val request = HttpRequest.newBuilder()
			.uri(URI.create(getResourceUrl(botProperties, fileName)))
			.build()
		val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
		return if (response.statusCode() == 200) response.body() else null
	}
}
