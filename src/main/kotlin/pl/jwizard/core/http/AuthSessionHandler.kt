/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.http

import javax.security.auth.login.LoginException
import pl.jwizard.core.bot.BotProperties
import pl.jwizard.core.log.AbstractLoggingBean
import org.springframework.stereotype.Component
import okhttp3.Request

@Component
class AuthSessionHandler(
	private val botProperties: BotProperties,
	private val httpClient: HttpClient,
) : AbstractLoggingBean(AuthSessionHandler::class) {

	private var sessionTokens: TokenResDto? = null

	fun loginAndCreateSession() {
		val instance = botProperties.instance
		val requestBody = httpClient.prepareRequestObject(
			Pair("appId", instance.appId),
			Pair("appSecret", instance.authToken),
		)
		val request = Request.Builder()
			.url(ApiUrl.STANDALONE_LOGIN.getUrl(botProperties))
			.post(requestBody)
			.build()

		val response = httpClient.makeBlockCall(request)
		if (response.code != 200) {
			throw LoginException(response.body?.string())
		}
		sessionTokens = httpClient.mapResponseObject(response, TokenResDto::class)
		httpClient.initSecureHttpClient(this)
		log.info("Successfully make session connection with API bridge")
	}

	fun updateTokens(tokens: TokenResDto) {
		sessionTokens = tokens
	}

	val authTokens: TokenResDto? get() = sessionTokens
}
