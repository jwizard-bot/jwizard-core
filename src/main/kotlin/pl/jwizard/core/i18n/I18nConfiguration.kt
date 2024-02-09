/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.i18n

import java.nio.charset.StandardCharsets
import pl.jwizard.core.log.AbstractLoggingBean
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.support.ResourceBundleMessageSource

@Configuration
class I18nConfiguration(
	private val i18nProperties: I18nProperties
) : AbstractLoggingBean(I18nConfiguration::class) {

	@Primary
	@Bean
	fun messageSource(): MessageSource {
		val source = ResourceBundleMessageSource()
		source.addBasenames(*createLocaleBundlePaths())
		source.setDefaultEncoding(StandardCharsets.UTF_8.name())
		return source
	}

	private fun createLocaleBundlePaths(): Array<String> {
		val basenames = arrayOfNulls<String>(i18nProperties.localeBundles.size + 1)
		basenames[0] = "i18n/messages"
		i18nProperties.localeBundles.toTypedArray().copyInto(basenames, destinationOffset = 1)
		log.info("Successfully loaded messageSource bean context: {}", basenames)
		return basenames.requireNoNulls()
	}
}
