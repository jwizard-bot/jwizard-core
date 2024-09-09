/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import org.springframework.core.env.PropertySource
import java.util.*
import kotlin.reflect.KClass

/**
 * Abstract class representing a source of loadable properties, with a specific type [T].
 *
 * @param T The type of the property source.
 * @property clazz The class of the property source.
 * @author Miłosz Gilga
 * @see PropertySource
 */
abstract class PropertySourceData<T>(
	private val clazz: KClass<*>,
) : PropertySource<T>(clazz.java.simpleName), PropertySourceLoader {

	/**
	 * The properties loaded from the property source.
	 */
	val properties: Properties = Properties()

	/**
	 * Loads properties into the internal properties map.
	 */
	fun loadProperties() {
		val propertiesAsMap = this.setProperties()
		propertiesAsMap.forEach { properties[it.key] = it.value }
	}

	/**
	 * Abstract method for setting properties. Implementations must provide the logic to load properties.
	 *
	 * @return A map of properties.
	 */
	protected abstract fun setProperties(): Map<Any, Any>
}
