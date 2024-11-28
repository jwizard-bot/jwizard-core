/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

import pl.jwizard.jwc.core.property.EnvironmentBean
import java.io.InputStream

/**
 * Abstract class responsible for retrieving resources based on a specified path.
 *
 * The class provides a method to retrieve resources as a pair containing the resource's filename and an [InputStream]
 * for accessing the resource's contents.
 *
 * @property environment Provides access to application environment properties.
 * @author Miłosz Gilga
 */
abstract class ResourceRetriever(private val environment: EnvironmentBean) {

	/**
	 * Retrieves a resource based on the specified [ResourceObject] and optional arguments.
	 *
	 * The method formats the resource path using [args] and then retrieves an InputStream for that path. Returns a [Pair]
	 * where the first element is the filename extracted from the resource path and the second element is the
	 * [InputStream] of the resource.
	 *
	 * @param resourceObject An object representing the resource path template.
	 * @param args Optional arguments for formatting the resource path.
	 * @return A [Pair] containing the resource filename as a [String] and an [InputStream] to the resource contents.
	 */
	fun getObject(resourceObject: ResourceObject, vararg args: String): Pair<String?, InputStream?> {
		val resourcePath = resourceObject.resourcePath.format(*args)
		val inputStream = retrieveObject(resourcePath)
		return Pair(resourcePath.substringAfterLast("/"), inputStream)
	}

	/**
	 * Retrieves an InputStream for a resource specified by [resourcePath].
	 *
	 * This method is abstract and should be implemented by subclasses to provide the specific retrieval logic for the
	 * resource.
	 *
	 * @param resourcePath The full path of the resource to be retrieved.
	 * @return An [InputStream] for accessing the resource contents, or null if retrieval fails.
	 */
	protected abstract fun retrieveObject(resourcePath: String): InputStream?
}
