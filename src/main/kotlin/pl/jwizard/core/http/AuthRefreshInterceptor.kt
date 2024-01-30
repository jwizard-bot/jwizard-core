/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.http

import pl.jwizard.core.bot.BotProperties
import pl.jwizard.core.utils.AbstractLoggingBean
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthRefreshInterceptor(
	private val httpClient: HttpClient,
	private val authSessionHandler: AuthSessionHandler,
	private val botProperties: BotProperties,
) : AbstractLoggingBean(AuthRefreshInterceptor::class), Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {
		val req = chain.request()
		val (access, refresh) = authSessionHandler.authTokens ?: return chain.proceed(req)

		val authRequest = req.newBuilder()
			.header(AUTHORIZATION_HEADER, "Bearer $access")
			.build()

		val authResponse = chain.proceed(authRequest)
		if (authResponse.code == 401) {
			val requestBody = httpClient.prepareRequestObject(
				Pair("expiredAccessToken", access),
				Pair("refreshToken", refresh),
			)
			val request = Request.Builder()
				.url(ApiUrl.STANDALONE_REFRESH.getUrl(botProperties))
				.post(requestBody)
				.build()

			val response = httpClient.makeBlockCall(request)
			val body = httpClient.mapResponseObject(response, TokenResDto::class) ?: return chain.proceed(req)

			authSessionHandler.updateTokens(body)

			val recallRequest = req.newBuilder()
				.header(AUTHORIZATION_HEADER, "Bearer ${body.accessToken}")
				.build()

			log.info("Recall request for URL: {} with refreshed tokens", authRequest.url)
			return chain.proceed(recallRequest)
		}
		return authResponse
	}

	companion object {
		const val AUTHORIZATION_HEADER = "Authorization"
	}
}
