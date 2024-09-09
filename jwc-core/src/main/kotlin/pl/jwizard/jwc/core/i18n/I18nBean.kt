/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Component
import java.util.*

/**
 * A Spring Bean responsible for internationalization (i18n) of messages. It uses the [MessageSource] to fetch messages
 * based on locale and replaces placeholders with provided parameters.
 *
 * @property messageSource The [MessageSource] used to resolve messages.
 * @constructor Creates an [I18nBean] with the specified `MessageSource`.
 * @author Miłosz Gilga
 */
@Component
class I18nBean(private val messageSource: MessageSource) {

	companion object {
		/**
		 * The delimiter used to mark the beginning of a placeholder in a message.
		 */
		private const val START_DELIMITER = "{{"

		/**
		 * The delimiter used to mark the end of a placeholder in a message.
		 */
		private const val END_DELIMITER = "}}"
	}

	/**
	 * Retrieves and formats a localized message based on the provided key and parameters. The message is fetched from
	 * the [MessageSource] for the specified language. Any placeholders in the message are replaced with the
	 * corresponding values from the [params] map.
	 *
	 * @param rawKey The key for the message to retrieve from the [MessageSource].
	 * @param params A map of placeholder keys and their corresponding values for message formatting.
	 * @param lang The language code to determine the locale for the message.
	 * @return The formatted message string, or the raw key if the message is not found.
	 */
	fun tRaw(rawKey: String, params: Map<String, Any>, lang: String): String {
		val locale = Locale.forLanguageTag(lang)
		return try {
			var propertyValue = messageSource.getMessage(rawKey, null, locale)
			if (propertyValue.isBlank()) {
				propertyValue
			} else {
				for ((key, value) in params) {
					propertyValue = propertyValue.replace("$START_DELIMITER${key}$END_DELIMITER", value.toString())
				}
				propertyValue
			}
		} catch (ex: NoSuchMessageException) {
			rawKey
		}
	}

	/**
	 * Retrieves a localized message based on the provided key and language without any additional formatting
	 * parameters. This method is a convenience wrapper around [tRaw] that uses an empty map for parameters.
	 *
	 * @param rawKey The key for the message to retrieve from the [MessageSource].
	 * @param lang The language code to determine the locale for the message.
	 * @return The localized message string.
	 */
	fun tRaw(rawKey: String, lang: String) = tRaw(rawKey, emptyMap(), lang)

	/**
	 * Retrieves a localized message based on the provided [I18nLocaleSource], parameters, and language.
	 * This method looks up the message using the [MessageSource], replaces placeholders with the provided parameters,
	 * and returns the final formatted message.
	 *
	 * @param i18nLocaleSource The [I18nLocaleSource] that provides the placeholder key for the message.
	 * @param params A map of parameters to replace placeholders in the message.
	 * @param lang The language tag representing the desired locale (ex. *en*).
	 * @return The formatted localized message with placeholders replaced by the corresponding parameters.
	 */
	fun t(i18nLocaleSource: I18nLocaleSource, params: Map<String, Any>, lang: String) =
		tRaw(i18nLocaleSource.getPlaceholder(), params, lang)

	/**
	 * Retrieves a localized message based on the provided [I18nLocaleSource] and language. This method uses an empty
	 * map for parameters.
	 *
	 * @param i18nLocaleSource The [I18nLocaleSource] that provides the placeholder key for the message.
	 * @param lang The language tag representing the desired locale (ex. *en*).
	 * @return The localized message with placeholders replaced by the corresponding parameters.
	 */
	fun t(i18nLocaleSource: I18nLocaleSource, lang: String) = t(i18nLocaleSource, emptyMap(), lang)
}
