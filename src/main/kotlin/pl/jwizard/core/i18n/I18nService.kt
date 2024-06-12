/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.i18n

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Component
import java.util.*

@Component
class I18nService(
	private val messageSource: MessageSource
) {
	fun getMessage(i18nLocale: I18nLocale, params: Map<String, Any>, lang: String): String {
		val locale = Locale.forLanguageTag(lang)
		var text: String
		try {
			text = messageSource.getMessage(i18nLocale.getPlaceholder(), null, locale)
			if (text.isBlank()) {
				return text
			}
			for ((key, value) in params) {
				text = text.replace("{{${key}}}", value.toString())
			}
		} catch (ex: NoSuchMessageException) {
			return i18nLocale.getPlaceholder()
		}
		return text
	}

	fun getMessage(i18nLocale: I18nLocale, lang: String) = getMessage(i18nLocale, mapOf(), lang)
}
