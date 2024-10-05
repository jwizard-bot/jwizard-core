/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

/**
 * A Spring component that initializes the Jackson ObjectMapper. This class provides a configured instance of
 * [ObjectMapper] with specific serialization settings.
 *
 * @author Miłosz Gilga
 */
@Component
class JacksonInitializerBean {

	/**
	 * Creates and configures an ObjectMapper bean. This method sets up the [ObjectMapper] to avoid writing dates as
	 * timestamps.
	 *
	 * @return A configured instance of [ObjectMapper].
	 */
	@Bean
	fun objectMapper(): ObjectMapper {
		val objectMapper = ObjectMapper()
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		return objectMapper
	}
}
