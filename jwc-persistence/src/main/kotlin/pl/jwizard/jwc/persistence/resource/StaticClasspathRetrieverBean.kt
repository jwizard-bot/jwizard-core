package pl.jwizard.jwc.persistence.resource

import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.util.findResourceInMultipleContainers
import pl.jwizard.jwl.util.logger
import java.io.IOException
import java.io.InputStream

@SingletonComponent
class StaticClasspathRetrieverBean(environment: EnvironmentBean) : ResourceRetriever() {

	companion object {
		private val log = logger<HttpResourceRetrieverBean>()
	}

	private val prefixes = environment
		.getListProperty<String>(AppBaseListProperty.STATIC_RESOURCES_PREFIXES)

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
