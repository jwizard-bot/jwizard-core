/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.config

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

/**
 * Contains beans related to HTTP communication for the Spring application context.
 *
 * @author Miłosz Gilga
 */
@Component
class HttpInitializerBean {

	/**
	 * Provides a [RestTemplate] bean for the Spring application context.
	 *
	 * This method creates and returns a new instance of [RestTemplate]. The returned [RestTemplate] can be used to
	 * perform HTTP requests in other parts of the application.
	 *
	 * @return A [RestTemplate] instance configured with default settings.
	 */
	@Bean
	fun restTemplate() = RestTemplate()
}
