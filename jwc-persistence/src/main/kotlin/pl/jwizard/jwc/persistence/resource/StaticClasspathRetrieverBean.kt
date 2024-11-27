/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

import org.springframework.core.io.ClassPathResource
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.util.findResourceInMultipleContainers
import pl.jwizard.jwl.util.logger
import java.io.IOException
import java.io.InputStream

/**
 * Implementation of [ResourceRetriever] that retrieves resources specifically from the classpath. This class uses
 * Spring's [ClassPathResource] to access resources located in the classpath of the project.
 *
 * @param environment Provides access to application environment properties.
 * @author Miłosz Gilga
 */
@SingletonComponent
class StaticClasspathRetrieverBean(environment: EnvironmentBean) : ResourceRetriever(environment) {

	companion object {
		private val log = logger<HttpResourceRetrieverBean>()
	}

	/**
	 * Prefix used for accessing static resources in the application.
	 */
	private val prefixes = environment.getListProperty<String>(AppBaseListProperty.STATIC_RESOURCES_PREFIXES)

	/**
	 * Retrieves an InputStream for a resource located in the classpath, specified by the [resourcePath]. Adds a leading
	 * "/" to the [resourcePath] if not already present, and logs an error if the resource cannot be accessed due to an
	 * [IOException].
	 *
	 * @param resourcePath The relative path of the resource within the classpath.
	 * @return An [InputStream] to read the contents of the resource, or null if retrieval fails.
	 */
	override fun retrieveObject(resourcePath: String): InputStream? {
		val resource = findResourceInMultipleContainers(prefixes, resourcePath)
		return try {
			resource?.inputStream
		} catch (ex: IOException) {
			log.error(ex.message)
			null
		}
	}
}
