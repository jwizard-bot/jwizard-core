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
	 * Creates a new [PropertySourcesPropertyResolver] based on the current set of property sources.
	 */
	val resolver = PropertySourcesPropertyResolver(propertySources)

	/**
	 * Adds a new property source to the environment and updates the property resolver.
	 *
	 * This method takes a [PropertySourceData]] loader, loads its properties, and adds the source to the
	 * [propertySources] collection. It also updates the `size` property with the number of newly added properties, if
	 * the loader is not an instance of [PropertyValueExtractor] (extractor replacing values instead adding new).
	 *
	 * The method then creates and returns a new instance of [PropertySourcesPropertyResolver] that is
	 * based on the updated set of property sources.
	 *
	 * @param T The type of properties that the loader is responsible for.
	 * @param loader The [PropertySourceData] instance used to load and provide properties.
	 * @return A [PropertySourcesPropertyResolver] instance that reflects the updated property sources.
	 */
	fun <T> addSource(loader: PropertySourceData<T>): PropertySourcesPropertyResolver {
		loader.loadProperties()
		propertySources.addLast(loader.sourceLoader)
		propertySourceNames.add(loader.javaClass.simpleName)
		if (loader !is PropertyValueExtractor) {
			size += loader.properties.size
		}
		return resolver
	}
}
