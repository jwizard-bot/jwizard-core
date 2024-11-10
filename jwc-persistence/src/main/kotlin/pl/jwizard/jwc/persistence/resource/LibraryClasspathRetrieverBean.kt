/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

import org.springframework.core.io.ClassPathResource
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger
import java.io.IOException
import java.io.InputStream

/**
 * Implementation of [ResourceRetriever] that retrieves resources specifically from the classpath. This class uses
 * Spring's [ClassPathResource] to access resources located in the classpath of the project.
 *
 * @author Miłosz Gilga
 * @see ResourceRetriever
 */
@SingletonComponent
class LibraryClasspathRetrieverBean : ResourceRetriever() {

	companion object {
		private val log = logger<HttpResourceRetrieverBean>()
	}

	/**
	 * Retrieves an InputStream for a resource located in the classpath, specified by the [resourcePath]. Adds a leading
	 * "/" to the [resourcePath] if not already present, and logs an error if the resource cannot be accessed due to an
	 * [IOException].
	 *
	 * @param resourcePath The relative path of the resource within the classpath.
	 * @return An [InputStream] to read the contents of the resource, or null if retrieval fails.
	 */
	override fun retrieveObject(resourcePath: String): InputStream? {
		val resourceUri = if (!resourcePath.startsWith("/")) "/$resourcePath" else resourcePath
		val resource = ClassPathResource(resourceUri, IoCKtContextFactory::class.java)
		return try {
			resource.inputStream
		} catch (ex: IOException) {
			log.error(ex.message)
			null
		}
	}
}
