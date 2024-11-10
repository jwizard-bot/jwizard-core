/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
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
 * Bean for retrieving resources via HTTP. Uses [HttpClient] to fetch resources from an HTTP-based API.
 *
 * @property environmentBean Contains environment-specific properties such as API URLs.
 * @author Miłosz Gilga
 * @see ResourceRetriever
 */
@SingletonComponent
class HttpResourceRetrieverBean(private val environmentBean: EnvironmentBean) : ResourceRetriever() {

	companion object {
		private val log = logger<HttpResourceRetrieverBean>()
	}

	/**
	 * The base URL for accessing the static resources URL, used to construct the complete URL for fetching public
	 * resources.
	 */
	private val staticResourcesUrl = environmentBean.getProperty<String>(AppBaseProperty.STATIC_RESOURCES_URL)

	/**
	 * The [HttpClient] used for making HTTP requests to retrieve resources.
	 */
	private val httpClient = HttpClient.newHttpClient()

	init {
		log.info("Init HTTP resource retriever with path: {}.", staticResourcesUrl)
	}

	/**
	 * Retrieves a resource from an HTTP URL and returns an [InputStream] for reading its contents.
	 *
	 * Constructs the complete URL using the base URL and [resourcePath]. Sends an HTTP GET request using [HttpClient]
	 * and converts the response body to an [InputStream] if successful. Logs an error and returns `null` if the resource
	 * could not be retrieved (ex. if the response status is not 200).
	 *
	 * @param resourcePath The path to the specific resource, appended to the base URL.
	 * @return An [InputStream] of the resource content, or `null` if an error occurred or resource is unavailable.
	 */
	override fun retrieveObject(resourcePath: String): InputStream? {
		val resourceUrl = "$staticResourcesUrl/${resourcePath}"
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
