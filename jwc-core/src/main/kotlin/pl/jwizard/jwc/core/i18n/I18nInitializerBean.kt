/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n

import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.i18n.spi.LanguageSupplier
import pl.jwizard.jwc.core.property.BotMultiProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.s3.S3ClientBean
import java.nio.charset.StandardCharsets

/**
 * This component initializes the [MessageSource] bean used for internationalization (i18n) in the application.
 * It sets up the [CombinedMessageSource] with the necessary configuration to handle messages from both
 * local resources and remote S3 sources.
 *
 * @property environmentBean A Spring-managed bean that provides environment-specific properties used to configure
 * 					 the message source and S3 client.
 * @author Miłosz Gilga
 * @see S3ClientBean
 * @see LanguageSupplier
 * @see CombinedMessageSource
 */
@Component
class I18nInitializerBean(private val environmentBean: EnvironmentBean) {

	companion object {
		private val log = LoggerFactory.getLogger(I18nInitializerBean::class.java)

		/**
		 * The default base name for the i18n message bundle.
		 */
		private const val DEFAULT_I18N_BUNDLE_NAME = "i18n/messages"
	}

	/**
	 * Configures and provides the [MessageSource] bean used for internationalization. This method creates an instance
	 * of [CombinedMessageSource], sets the base names for message bundles, and configures cache settings and encoding.
	 * It combines local and remote (S3) message sources.
	 *
	 * @param s3ClientBean The S3 client bean used to access message files stored in S3.
	 * @param languageSupplier The supplier of language information to be used in message resolution.
	 * @return An instance of [MessageSource] configured with local and S3 message sources.
	 */
	@Bean
	fun messageSource(s3ClientBean: S3ClientBean, languageSupplier: LanguageSupplier): MessageSource {
		val source = CombinedMessageSource(
			environmentBean,
			s3ClientBean,
			languageSupplier,
		)
		source.addBasenames(*createLocaleBundlePaths())
		source.setCacheSeconds(3600)
		source.setDefaultEncoding(StandardCharsets.UTF_8.name())
		return source
	}

	/**
	 * Creates an array of base names for locale-specific message bundles. It includes both the default message bundle
	 * path and any additional paths specified in the environment properties.
	 *
	 * @return An array of base names for message bundles.
	 */
	private fun createLocaleBundlePaths(): Array<String> {
		val localMessageBundles = environmentBean.getMultiProperty<String>(BotMultiProperty.I18N_RESOURCES_LOCALE)
		val basenameBundles = arrayOfNulls<String>(localMessageBundles.size + 1)
		basenameBundles[0] = DEFAULT_I18N_BUNDLE_NAME
		localMessageBundles.toTypedArray().copyInto(basenameBundles, destinationOffset = 1)
		log.info("Load local messageSource bean context: {}.", basenameBundles)
		return basenameBundles.filterNotNull().toTypedArray()
	}
}
