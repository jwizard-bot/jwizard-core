/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.http

import kotlin.reflect.KClass
import pl.jwizard.core.bot.BotProperties
import pl.jwizard.core.log.AbstractLoggingBean
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

@Component
class HttpClient(
	private val objectMapper: ObjectMapper,
	private val okHttpClient: OkHttpClient,
	private val botProperties: BotProperties,
) : AbstractLoggingBean(HttpClient::class) {

	private var secureOkHttpClient: OkHttpClient? = null

	fun initSecureHttpClient(authSessionHandler: AuthSessionHandler) {
		secureOkHttpClient = OkHttpClient()
			.newBuilder()
			.addInterceptor(AuthRefreshInterceptor(this, authSessionHandler, botProperties))
			.build()
	}

	fun prepareRequestObject(vararg data: Pair<String, String>): RequestBody = objectMapper
		.writeValueAsString(mapOf(*data))
		.toRequestBody("application/json; charset=UTF-8".toMediaTypeOrNull())

	fun <T : Any> mapResponseObject(response: Response, clazz: KClass<T>): T? {
		if (response.body == null) {
			return null
		}
		return objectMapper.readValue(response.body?.string(), clazz.java)
	}

	fun makeBlockCall(req: Request): Response = makeBlockCall(req, okHttpClient)

	fun makeSecureBlockCall(req: Request): Response = makeBlockCall(req, secureOkHttpClient!!)

	private fun makeBlockCall(req: Request, variousHttpClient: OkHttpClient): Response {
		val call = variousHttpClient.newCall(req)
		return call.execute()
	}
}
