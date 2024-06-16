/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.http

import com.google.gson.Gson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
class HttpConfiguration {
	@Bean
	fun httpClient(): HttpClient = HttpClient.newHttpClient()

	@Bean
	fun gsonParser(): Gson = Gson()
}
