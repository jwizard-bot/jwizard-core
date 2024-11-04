/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.util.logger
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Bean for retrieving resources via HTTP. Uses Spring's [HttpClient] to fetch resources from an HTTP-based API.
 *
 * @property environmentBean Contains environment-specific properties such as API URLs.
 * @author Miłosz Gilga
 * @see ResourceRetriever
 */
@Component
class HttpResourceRetrieverBean(private val environmentBean: EnvironmentBean) : ResourceRetriever() {

	companion object {
		private val log = logger<HttpResourceRetrieverBean>()
	}

	/**
	 * The base URL for accessing the static resources URL, used to construct the complete URL for fetching public
	 * resources.
	 */
	final val staticResourcesUrl = environmentBean.getProperty<String>(AppBaseProperty.STATIC_RESOURCES_URL)

	/**
	 * The [HttpClient] used for making HTTP requests to retrieve resources.
	 */
	private val httpClient = HttpClient.newHttpClient()

	init {
		log.info("Init HTTP resource retriever with path: {}.", staticResourcesUrl)
	}

	/**
	 * Fetches a resource from the HTTP API as an [InputStream].
	 *
	 * @param resourceObject Specifies which resource to retrieve.
	 * @param args Additional arguments to format the resource path.
	 * @return An [InputStream] containing the resource, or `null` if it could not be retrieved.
	 */
	override fun getObject(resourceObject: ResourceObject, vararg args: String): InputStream? {
		val resourceUrl = "$staticResourcesUrl/${parseResourcePath(resourceObject, *args)}"
		return try {
			val request = HttpRequest.newBuilder()
				.uri(URI.create(resourceUrl))
				.GET()
				.build()
			val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())
			if (response.statusCode() != 200) {
				throw IOException("Could not found resource with url: $resourceUrl.")
			}
			ByteArrayInputStream(response.body())
		} catch (ex: IOException) {
			log.error(ex.message)
			null
		}
	}
}
