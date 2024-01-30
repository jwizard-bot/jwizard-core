/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.http

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import okhttp3.OkHttpClient

@Configuration
class HttpBeans {
	@Bean
	fun okHttpClient() = OkHttpClient()
}
