/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import org.springframework.core.env.PropertySource
import java.util.*
import kotlin.reflect.KClass

/**
 * Abstract class representing a source of loadable properties.
 *
 * @property clazz The class of the property source.
 * @author Miłosz Gilga
 * @see PropertySource
 */
abstract class PropertySourceData(
	private val clazz: KClass<*>,
) : PropertySource<Properties>(clazz.java.simpleName, Properties()) {

	/**
	 * Loads properties into the internal properties map.
	 */
	fun loadProperties() {
		val propertiesAsMap = this.setProperties()
		propertiesAsMap.forEach { source[it.key] = it.value }
	}

	/**
	 * Retrieves a specific property by its name.
	 *
	 * @param name The name of the property to retrieve.
	 * @return The value of the property or `null` if not found.
	 */
	override fun getProperty(name: String): Any? = source[name]

	/**
	 * Abstract method for setting properties. Implementations must provide the logic to load properties.
	 *
	 * @return A map of properties.
	 */
	protected abstract fun setProperties(): Map<Any, Any>
}
