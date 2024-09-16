/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertySourcesPropertyResolver
import pl.jwizard.jwc.core.property.extractor.PropertyValueExtractor

/**
 * Manages and resolves properties in a Spring environment.
 *
 * This class allows for adding property sources and creating a property resolver based on the current set of
 * property sources.
 *
 * @property propertySources The mutable collection of property sources managed by this instance.
 * @author Miłosz Gilga
 */
class PropertiesEnvironment(private val propertySources: MutablePropertySources) {

	/**
	 * The number of properties currently loaded into the environment.
	 * This value is updated whenever new properties are added.
	 */
	var size = 0
		private set

	/**
	 * A mutable list of names of the property sources currently managed by this instance.
	 */
	val propertySourceNames = mutableListOf<String>()

	/**
	 * The [PropertySourcesPropertyResolver] instance used to resolve property values from the environment's property
	 * sources.
	 *
	 * This resolver provides methods to retrieve property values based on the keys. It is created and updated
	 * whenever a new property source is added to the environment.
	 */
	var resolver = PropertySourcesPropertyResolver(propertySources)
		private set

	/**
	 * Adds a new property source to the environment and updates the property resolver.
	 *
	 * This method takes a [PropertySourceData] loader, loads its properties, and adds the source to the
	 * [propertySources] collection. It also updates the `size` property with the number of newly added properties, if
	 * the loader is not an instance of [PropertyValueExtractor] (extractor replacing values instead adding new).
	 *
	 * @param sourceData The [PropertySourceData] instance used to load and provide properties.
	 */
	fun addSource(sourceData: PropertySourceData) {
		sourceData.loadProperties()
		propertySources.addLast(sourceData)
		propertySourceNames.add(sourceData.javaClass.simpleName)
		if (sourceData !is PropertyValueExtractor) {
			size += sourceData.source.size
		}
		resolver = createResolver()
	}

	/**
	 * Creates a new [PropertySourcesPropertyResolver] instance based on the current set of property sources.
	 *
	 * @return A new [PropertySourcesPropertyResolver] instance.
	 */
	fun createResolver() = PropertySourcesPropertyResolver(propertySources)
}
