/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.util.logger
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Bean for retrieving resources via HTTP. Uses Spring's [RestTemplate] to fetch resources from an HTTP-based API.
 *
 * @property environmentBean Contains environment-specific properties such as API URLs.
 * @property restTemplate The [RestTemplate] used for making HTTP requests to retrieve resources.
 * @author Miłosz Gilga
 * @see ResourceRetriever
 */
@Component
class HttpResourceRetrieverBean(
	private val environmentBean: EnvironmentBean,
	private val restTemplate: RestTemplate,
) : ResourceRetriever() {

	companion object {
		private val log = logger<HttpResourceRetrieverBean>()
	}

	/**
	 * The base URL for accessing the public S3 API, used to construct the complete URL for fetching public resources.
	 */
	final val s3PublicApiUrl = environmentBean.getProperty<String>(BotProperty.S3_PUBLIC_API_URL)

	init {
		log.info("Init HTTP resource retriever with path: {}.", s3PublicApiUrl)
	}

	/**
	 * Fetches a resource from the HTTP API as an [InputStream].
	 *
	 * @param resourceObject Specifies which resource to retrieve.
	 * @param args Additional arguments to format the resource path.
	 * @return An [InputStream] containing the resource, or `null` if it could not be retrieved.
	 */
	override fun getObject(resourceObject: ResourceObject, vararg args: String): InputStream? {
		val resourceUrl = "$s3PublicApiUrl/${parseResourcePath(resourceObject, *args)}"
		return try {
			val resourceBytes = restTemplate.getForObject(resourceUrl, ByteArray::class.java)
			resourceBytes?.let { ByteArrayInputStream(it) }
		} catch (ex: RestClientException) {
			log.error(ex.message)
			null
		}
	}
}
