package pl.jwizard.jwc.persistence.resource

import java.io.InputStream

abstract class ResourceRetriever {
	fun getObject(resourceObject: ResourceObject, vararg args: String): Pair<String?, InputStream?> {
		val resourcePath = resourceObject.resourcePath.format(*args)
		val inputStream = retrieveObject(resourcePath)
		return Pair(resourcePath.substringAfterLast("/"), inputStream)
	}

	protected abstract fun retrieveObject(resourcePath: String): InputStream?
}
