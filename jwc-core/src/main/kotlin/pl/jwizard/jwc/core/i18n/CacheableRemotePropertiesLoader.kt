/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n

import org.slf4j.LoggerFactory
import pl.jwizard.jwc.core.jvm.JvmThreadExecutor
import pl.jwizard.jwc.core.s3.S3ClientBean
import pl.jwizard.jwc.core.s3.S3Object
import java.io.StringReader
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * A class that loads and caches remote properties from S3 buckets. It extends [JvmThreadExecutor] to run periodic
 * updates in a scheduled manner. It fetches property files from remote sources and caches them for fast access.
 *
 * @property s3ClientBean The S3 client used to fetch remote property files.
 * @property languageTags A set of language tags for which properties are loaded.
 * @property remoteBundles A list of remote bundle identifiers for fetching property files.
 * @property charset The character set used for decoding the property files.
 * @author Miłosz Gilga
 * @see JvmThreadExecutor
 */
class CacheableRemotePropertiesLoader(
	private val s3ClientBean: S3ClientBean,
	private val languageTags: Set<String>,
	private val remoteBundles: List<String>,
	private val charset: Charset,
) : JvmThreadExecutor() {

	companion object {
		private val log = LoggerFactory.getLogger(CacheableRemotePropertiesLoader::class.java)
	}

	/**
	 * A thread-safe map storing properties for each language fetched from remote sources.
	 */
	val remoteProperties = ConcurrentHashMap<String, Properties>()

	/**
	 * A flag indicating whether the properties have been initialized.
	 */
	private var isInitialized = false

	/**
	 * The task executed by the thread executor. It fetches and updates properties from remote bundles and logs the
	 * number of properties loaded. It initializes the properties map if not already done.
	 */
	override fun run() {
		var countOfElements = 0
		val s3Resources = prepareS3ResourcePaths(remoteBundles)
		s3Resources.forEach { (lang, rawContent) ->
			rawContent?.let { countOfElements = insertProperties(lang, it) }
		}
		if (!isInitialized) {
			log.info(
				"Load: {} keys from remote bundles: {} with languages: {}.",
				countOfElements,
				remoteBundles,
				s3Resources.keys
			)
			isInitialized = true
		}
	}

	/**
	 * Prepares the paths to S3 resources for each language by fetching them from the S3 client. This method constructs
	 * a map of language codes to raw [String] for the remote message bundles.
	 *
	 * @param remoteBundles A list of remote bundle identifiers.
	 * @return A map of language codes to raw [String] for the remote message bundles.
	 */
	private fun prepareS3ResourcePaths(remoteBundles: List<String>) = languageTags
		.flatMap { language ->
			remoteBundles.map { language to s3ClientBean.getObjectAsText(S3Object.I18N_BUNDLE, charset, it, language) }
		}.toMap()

	/**
	 * Loads properties from the provided raw [String] and stores them in the [remoteProperties] map under
	 * the specified language code. Returns the number of properties loaded.
	 *
	 * @param language The language code for which the properties are loaded.
	 * @param rawContent The raw [String] containing the properties.
	 * @return The number of properties loaded from the raw [String].
	 */
	private fun insertProperties(language: String, rawContent: String): Int {
		val properties = Properties()
		properties.load(StringReader(rawContent))
		remoteProperties[language] = properties
		return properties.size
	}
}
