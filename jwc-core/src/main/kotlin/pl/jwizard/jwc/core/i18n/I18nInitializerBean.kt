/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n

import org.springframework.beans.factory.DisposableBean
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.i18n.spi.I18nPropertyFilesSupplier
import pl.jwizard.jwc.core.i18n.spi.LanguageSupplier
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.util.logger
import java.nio.charset.StandardCharsets

/**
 * This component initializes the [MessageSource] bean used for internationalization (i18n) in the application.
 * It sets up the [CombinedMessageSource] with the necessary configuration to handle messages from both
 * local resources and remote sources.
 *
 * @property environmentBean A Spring-managed bean that provides environment-specific properties used to configure
 * 					 the message source and S3 client.
 * @author Miłosz Gilga
 * @see I18nPropertyFilesSupplier
 * @see LanguageSupplier
 * @see CombinedMessageSource
 */
@Component
class I18nInitializerBean(private val environmentBean: EnvironmentBean) : DisposableBean {

	companion object {
		private val log = logger<I18nInitializerBean>()

		/**
		 * The default base name for the i18n message bundle.
		 */
		private const val DEFAULT_I18N_BUNDLE_NAME = "i18n/messages"

		/**
		 * The default character set used for encoding message bundles.
		 */
		private val DEFAULT_CHARSET = StandardCharsets.UTF_8
	}

	/**
	 * A map of languages loaded from the remote source, where the key is the language tag (ex. *en*) and the value
	 * is the language name.
	 */
	val languages = mutableMapOf<String, String>()

	/**
	 * The [CombinedMessageSource] instance used to handle message bundles from local and remote sources.
	 */
	private lateinit var source: CombinedMessageSource

	/**
	 * Configures the [MessageSource] bean with the [CombinedMessageSource], setting up the required properties
	 * including base names, remote bundles, cache duration, and encoding.
	 *
	 * @param i18nPropertyFilesSupplier The remote property supplier used to fetch remote property files.
	 * @param languageSupplier A supplier that provides available languages.
	 * @return The configured [MessageSource] bean.
	 */
	@Bean
	fun messageSource(
		i18nPropertyFilesSupplier: I18nPropertyFilesSupplier,
		languageSupplier: LanguageSupplier
	): MessageSource {
		languages.putAll(languageSupplier.getLanguages())
		source = CombinedMessageSource(i18nPropertyFilesSupplier, languages.keys, DEFAULT_CHARSET)

		val remoteBundles = environmentBean.getListProperty<String>(BotListProperty.I18N_RESOURCES_REMOTE)
		val revalidateCacheSec = environmentBean.getProperty<Int>(BotProperty.I81N_REVALIDATE_CACHE_SEC)

		source.setCacheSeconds(revalidateCacheSec)
		source.setBasenames(*createLocaleBundlePaths().toTypedArray())
		source.setRemoteBasenames(*remoteBundles.toTypedArray())
		return source
	}

	/**
	 * Creates an array of base names for locale-specific message bundles. It includes both the default message bundle
	 * path and any additional paths specified in the environment properties.
	 *
	 * @return A list of base names for message bundles.
	 */
	private fun createLocaleBundlePaths(): List<String> {
		val localMessageBundles = environmentBean.getListProperty<String>(BotListProperty.I18N_RESOURCES_LOCALE)
		val basenameBundles = arrayOfNulls<String>(localMessageBundles.size + 1)
		basenameBundles[0] = DEFAULT_I18N_BUNDLE_NAME
		localMessageBundles.toTypedArray().copyInto(basenameBundles, destinationOffset = 1)
		log.info("Load local messageSource bean context: {}.", basenameBundles)
		return basenameBundles.filterNotNull()
	}

	/**
	 * Cleans up resources by calling the `destroy` method on the [CombinedMessageSource] instance.
	 * This method is called automatically by Spring when the bean is disposed.
	 */
	override fun destroy() = source.destroy()
}
