/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n

import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.i18n.source.I18nDynamicMod
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import java.util.*

/**
 * A Spring Bean responsible for internationalization (i18n) of messages. It uses the [MessageSource] to fetch messages
 * based on locale and replaces placeholders with provided parameters.
 *
 * @property messageSource The [MessageSource] used to resolve messages.
 * @property environmentBean An [EnvironmentBean] that provides environment-specific properties with i18n configuration.
 * @property i18nInitializerBean The [I18nInitializerBean] component storing global configuration for i18n.
 * @author Miłosz Gilga
 */
@Component
class I18nBean(
	private val messageSource: MessageSource,
	private val environmentBean: EnvironmentBean,
	private val i18nInitializerBean: I18nInitializerBean,
) {

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
		tRaw(i18nLocaleSource.placeholder, params, lang)

	/**
	 * Retrieves a localized message based on the provided [I18nLocaleSource] and language. This method uses an empty
	 * map for parameters.
	 *
	 * @param i18nLocaleSource The [I18nLocaleSource] that provides the placeholder key for the message.
	 * @param lang The language tag representing the desired locale (ex. *en*).
	 * @return The localized message with placeholders replaced by the corresponding parameters.
	 */
	fun t(i18nLocaleSource: I18nLocaleSource, lang: String) = t(i18nLocaleSource, emptyMap(), lang)

	/**
	 * Retrieves the name of a language based on the provided language tag.
	 *
	 * This function checks if the list of languages is empty. If it is, it uses the default language from the
	 * environment properties. If languages are available, it uses the provided language tag to find the corresponding
	 * name. If no match is found, it returns a question mark.
	 *
	 * @param languageTag The tag of the language to retrieve the name for (ex. *en*).
	 * @return The name of the language, or *?* if the language tag is not found.
	 */
	fun getLanguageName(languageTag: String): String {
		val languages = i18nInitializerBean.languages
		val key = if (languages.entries.isEmpty()) {
			environmentBean.getProperty(BotProperty.I18N_DEFAULT_LANGUAGE)
		} else {
			languageTag
		}
		return languages[key] ?: ""
	}

	/**
	 * Retrieves raw messages for all available languages based on the provided [I18nDynamicMod] and arguments.
	 *
	 * This method generates messages for each language by formatting the key from the provided [I18nDynamicMod] using
	 * the provided `args`, and then looks up the messages in all available languages.
	 *
	 * @param i18nDynamicMod The [I18nDynamicMod] enum that provides the key pattern for the messages.
	 * @param args Arguments to format the key pattern.
	 * @return A map where the keys are language tags and the values are the formatted localized messages.
	 */
	fun tRaw(i18nDynamicMod: I18nDynamicMod, args: Array<String>) = i18nInitializerBean.languages.keys.associateWith {
		tRaw(i18nDynamicMod, args, it)
	}

	/**
	 * Retrieves a raw message based on the provided [I18nDynamicMod], arguments, and language.
	 *
	 * This method formats the key from the provided [I18nDynamicMod] using the provided `args`, and then looks up the
	 * message in the specified language. It uses an empty map for parameters.
	 *
	 * @param i18nDynamicMod The [I18nDynamicMod] enum that provides the key pattern for the message.
	 * @param args Arguments to format the key pattern.
	 * @param lang The language tag representing the desired locale (ex. *en*).
	 * @return The formatted localized message with placeholders replaced by the corresponding parameters.
	 */
	fun tRaw(i18nDynamicMod: I18nDynamicMod, args: Array<String>, lang: String) =
		tRaw(i18nDynamicMod.key.format(*args), emptyMap(), lang)

	/**
	 * Retrieves a raw message based on the provided i18n key, parameters, and language.
	 *
	 * This method fetches the message using the [MessageSource] for the given `i18nKey` and `lang`. It then replaces
	 * placeholders in the message with the provided parameters. If the key is not found or if there is an exception,
	 * it returns the `i18nKey` itself.
	 *
	 * @param i18nKey The key used to look up the message.
	 * @param params A map of parameters to replace placeholders in the message.
	 * @param lang The language tag representing the desired locale (ex. *en*).
	 * @return The formatted localized message with placeholders replaced by the corresponding parameters.
	 */
	private fun tRaw(i18nKey: String, params: Map<String, Any>, lang: String): String {
		val locale = Locale.forLanguageTag(lang)
		return try {
			var propertyValue = messageSource.getMessage(i18nKey, null, locale)
			if (propertyValue.isBlank()) {
				propertyValue
			} else {
				for ((key, value) in params) {
					propertyValue = propertyValue.replace("$START_DELIMITER${key}$END_DELIMITER", value.toString())
				}
				propertyValue
			}
		} catch (ex: NoSuchMessageException) {
			i18nKey
		}
	}
}
