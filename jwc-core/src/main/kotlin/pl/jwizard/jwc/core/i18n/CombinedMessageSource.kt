/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n

import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import pl.jwizard.jwc.core.i18n.spi.LanguageSupplier
import pl.jwizard.jwc.core.property.BotMultiProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.s3.S3ClientBean
import pl.jwizard.jwc.core.s3.S3Object
import java.io.InputStream
import java.text.MessageFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * A custom [MessageSource] implementation that combines messages from local resources and remote (S3) sources.
 * This class extends [ReloadableResourceBundleMessageSource] to support dynamic loading of message bundles
 * from an S3-compatible storage service in addition to local properties files.
 *
 * @property environmentBean Provides environment-specific properties and configuration.
 * @property s3ClientBean The S3 client used to fetch remote message bundles.
 * @property languageSupplier Supplies available languages for fetching message bundles.
 * @author Miłosz Gilga
 */
class CombinedMessageSource(
	private val environmentBean: EnvironmentBean,
	private val s3ClientBean: S3ClientBean,
	private val languageSupplier: LanguageSupplier,
) : ReloadableResourceBundleMessageSource() {

	companion object {
		private val log = LoggerFactory.getLogger(CombinedMessageSource::class.java)
	}

	/**
	 * A thread-safe map storing properties for each language fetched from remote sources.
	 */
	private val remoteProperties = ConcurrentHashMap<String, Properties>()

	/**
	 * The total count of message keys loaded from remote bundles (per language).
	 */
	private var countOfElements = 0

	init {
		val remoteBundles = environmentBean.getMultiProperty<String>(BotMultiProperty.I18N_RESOURCES_REMOTE)
		val s3Resources = prepareS3ResourcePaths(remoteBundles)
		s3Resources.forEach { (lang, inputStream) ->
			inputStream.use { countOfElements = insertProperties(lang, it) }
		}
		log.info(
			"Load: {} keys from remote bundles: {} with languages: {}.",
			countOfElements,
			remoteBundles,
			s3Resources.keys
		)
	}

	/**
	 * Resolves a message with the specified code and locale. It first attempts to retrieve the message from the remote
	 * properties. If not found, it falls back to the parent implementation.
	 *
	 * @param code The message key to resolve.
	 * @param locale The locale to use for resolving the message.
	 * @return A [MessageFormat] object for the resolved message, or `null` if the message could not be resolved.
	 */
	override fun resolveCode(code: String, locale: Locale): MessageFormat? {
		val properties = remoteProperties[locale.language]
		if (properties != null) {
			val message = properties.getProperty(code) ?: return super.resolveCode(code, locale)
			return MessageFormat(message, locale)
		}
		return super.resolveCode(code, locale)
	}

	/**
	 * Resolves a message with the specified code and locale without arguments. It first attempts to retrieve the message
	 * from the remote properties. If not found, it falls back to the parent implementation.
	 *
	 * @param code The message key to resolve.
	 * @param locale The locale to use for resolving the message.
	 * @return The resolved message string, or `null` if the message could not be resolved.
	 */
	override fun resolveCodeWithoutArguments(code: String, locale: Locale): String? {
		val properties = remoteProperties[locale.language]
		if (properties != null) {
			return properties.getProperty(code) ?: super.resolveCodeWithoutArguments(code, locale)
		}
		return super.resolveCodeWithoutArguments(code, locale)
	}

	/**
	 * Prepares the paths to S3 resources for each language by fetching them from the S3 client. This method constructs
	 * a map of language codes to [InputStream] for the remote message bundles.
	 *
	 * @param remoteBundles A list of remote bundle identifiers.
	 * @return A map of language codes to [InputStream] for the remote message bundles.
	 */
	private fun prepareS3ResourcePaths(remoteBundles: List<String>): Map<String, InputStream?> {
		val s3Resources = mutableMapOf<String, InputStream?>()
		for (language in languageSupplier.fetchLanguages()) {
			for (remoteBundle in remoteBundles) {
				s3Resources[language] = s3ClientBean.getObject(S3Object.I18N_BUNDLE, remoteBundle, language)
			}
		}
		return s3Resources
	}

	/**
	 * Loads properties from the provided [InputStream] and stores them in the [remoteProperties] map under
	 * the specified language code. Returns the number of properties loaded.
	 *
	 * @param language The language code for which the properties are loaded.
	 * @param inputStream The [InputStream] containing the properties.
	 * @return The number of properties loaded from the [InputStream].
	 */
	private fun insertProperties(language: String, inputStream: InputStream?): Int {
		val properties = Properties()
		properties.load(inputStream)
		remoteProperties[language] = properties
		return properties.size
	}
}
