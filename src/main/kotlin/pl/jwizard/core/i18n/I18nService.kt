/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.i18n

import java.util.*
import pl.jwizard.core.settings.GuildSettings
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class I18nService(
	@Lazy private val guildSettings: GuildSettings,
	private val messageSource: MessageSource,
) {
	fun getMessage(i18nLocale: I18nLocale, params: Map<String, Any>, guildId: String?): String {
		if (guildId == null) {
			return i18nLocale.getPlaceholder()
		}
		val settings = guildSettings.getGuildProperties(guildId)
		val locale = Locale.forLanguageTag(settings.locale)
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

	fun getMessage(i18nLocale: I18nLocale, guildId: String?) = getMessage(i18nLocale, mapOf(), guildId)
}
