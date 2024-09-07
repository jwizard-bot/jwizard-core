/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import org.springframework.core.env.PropertySource
import kotlin.reflect.KClass

/**
 * Abstract base class for extracting property values from various sources.
 *
 * This class extends [PropertySourceData], providing common functionality for property extraction and property source
 * loading. It provides a mechanism to retrieve properties based on a qualifier prefix and handle default values.
 *
 * @param T The type of the property source data.
 * @property clazz The Kotlin class of the property source data.
 * @author Miłosz Gilga
 * @see PropertySourceData
 * @see PropertySourceLoader
 */
abstract class PropertyValueExtractor<T>(
	private val clazz: KClass<*>,
) : PropertySourceData<T>(clazz) {

	companion object {
		/**
		 * Separator used in property names to distinguish between key and value parts.
		 */
		private const val SEPARATOR = ":"
	}

	/**
	 * Retrieves the property value based on the given name.
	 *
	 * The property name should start with a qualifier prefix derived from the extraction key, followed by the actual
	 * property key and optionally a default value. If the property is not found, and no default value is provided, a
	 * [PropertyNotFoundException] is thrown.
	 *
	 * @param name The name of the property to retrieve.
	 * @return The property value or default value if not found.
	 */
	override fun getProperty(name: String): Any? {
		val qualifier = "${extractionKey()}$SEPARATOR"
		if (!name.startsWith(qualifier)) {
			return null
		}
		val keyFragments = name.substring(qualifier.length).split(SEPARATOR)
		val key = keyFragments[0]
		val parsedProperty = properties.getProperty(key)
		if (parsedProperty == null) {
			if (keyFragments.size != 2) { // has not default value
				throw PropertyNotFoundException(this::class, name)
			}
			return keyFragments[1]
		}
		return parsedProperty
	}

	/**
	 * Returns the [PropertySource] instance for this extractor.
	 *
	 * @return The [PropertySource] instance.
	 */
	override fun getSourceLoader(): PropertySource<*> = this

	/**
	 * Abstract method to define the extraction key used for identifying properties.
	 *
	 * @return The extraction key as a [String].
	 */
	protected abstract fun extractionKey(): String
}
