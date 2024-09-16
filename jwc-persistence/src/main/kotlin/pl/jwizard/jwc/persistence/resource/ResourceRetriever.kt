/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

import java.io.InputStream
import java.nio.charset.Charset

/**
 * Abstract class for retrieving resources from different storage systems. This class provides utility methods to
 * retrieve resources as byte arrays or text.
 *
 * @author Miłosz Gilga
 */
abstract class ResourceRetriever {

	/**
	 * Fetches a resource as a byte array.
	 *
	 * @param resourceObject Specifies which resource to retrieve.
	 * @param args Additional arguments to format the resource path.
	 * @return A byte array containing the resource, or `null` if the resource could not be retrieved.
	 */
	fun getObjectAsByteArray(resourceObject: ResourceObject, vararg args: String): ByteArray? =
		getObject(resourceObject, *args).let { it.use { inputStream -> inputStream?.readBytes() } }

	/**
	 * Fetches a resource as a string using the specified character set.
	 *
	 * @param resourceObject Specifies which resource to retrieve.
	 * @param charset The character set to use for decoding the resource.
	 * @param args Additional arguments to format the resource path.
	 * @return A [String] containing the resource content, or `null` if it could not be retrieved.
	 */
	fun getObjectAsText(resourceObject: ResourceObject, charset: Charset, vararg args: String): String? {
		val inputStream = getObject(resourceObject, *args)
		return inputStream?.bufferedReader(charset).use { it?.readText() }
	}

	/**
	 * Parses the resource path for the given [ResourceObject], replacing placeholders in the path with the provided
	 * arguments.
	 *
	 * @param resourceObject The [ResourceObject] whose path is to be parsed.
	 * @param args Arguments used to format the resource path.
	 * @return A string representing the formatted resource path.
	 */
	protected fun parseResourcePath(resourceObject: ResourceObject, vararg args: String) =
		resourceObject.resourcePath.format(*args)

	/**
	 * Fetches a resource as an [InputStream].
	 *
	 * @param resourceObject Specifies which resource to retrieve.
	 * @param args Additional arguments to format the resource path.
	 * @return An [InputStream] containing the resource, or `null` if the resource could not be retrieved.
	 */
	abstract fun getObject(resourceObject: ResourceObject, vararg args: String): InputStream?
}
