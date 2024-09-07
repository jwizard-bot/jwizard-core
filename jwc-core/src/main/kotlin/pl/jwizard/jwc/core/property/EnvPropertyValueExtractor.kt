/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import io.github.cdimascio.dotenv.Dotenv
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Extractor for environment property values from system environment variables, system properties, and an optional
 * .env file.
 *
 * @property envFileEnabled Flag indicating whether to load properties from the .env file.
 * @author Miłosz Gilga
 * @see PropertyValueExtractor
 */
class EnvPropertyValueExtractor(
	private val envFileEnabled: Boolean,
) : PropertyValueExtractor<EnvPropertyValueExtractor>(EnvPropertyValueExtractor::class) {

	/**
	 * Dotenv instance for reading .env file.
	 */
	private val dotEnv = Dotenv.load()

	companion object {
		private val log = LoggerFactory.getLogger(EnvPropertyValueExtractor::class.java)

		/**
		 * The default name of the environment file.
		 */
		private const val ENV_FILE_NAME = ".env"
	}

	/**
	 * Reads properties from the .env file if it exists and loading from the file is enabled.
	 *
	 * @return A map of properties loaded from the .env file.
	 */
	override fun setProperties(): Map<Any, Any> {
		val file = File(ENV_FILE_NAME)
		if (!file.exists() || !envFileEnabled) {
			if (!envFileEnabled) {
				log.info("Env file disabled. Skipping loading environment variables from {} file.", ENV_FILE_NAME)
			}
			return emptyMap()
		}
		val envFileKeys = file.readLines()
			.filter { line -> line.isNotBlank() && !line.startsWith("#") }
			.mapNotNull { line ->
				val key = line.split("=", limit = 2).firstOrNull()?.trim()
				if (key.isNullOrEmpty()) null else key
			}
			.toTypedArray()

		log.info("Extract: {} environment variables from: {} file: {}", envFileKeys.size, ENV_FILE_NAME, envFileKeys)
		return envFileKeys.associateWith { dotEnv.get(it as String) }
	}

	override fun extractionKey(): String = "env"
}
