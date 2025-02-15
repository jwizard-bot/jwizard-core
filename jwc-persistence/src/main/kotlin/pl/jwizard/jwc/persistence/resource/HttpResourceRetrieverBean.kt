package pl.jwizard.jwc.persistence.resource

import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SingletonComponent
internal class HttpResourceRetrieverBean : ResourceRetriever() {
	companion object {
		private val log = logger<HttpResourceRetrieverBean>()
	}

	private val httpClient = HttpClient.newHttpClient()

	override fun retrieveObject(resourcePath: String): InputStream? {
		return try {
			val request = HttpRequest.newBuilder()
				.uri(URI.create(resourcePath))
				.build()
			val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())
			if (response.statusCode() != 200) {
				throw IOException("Could not found resource with url: $resourcePath.")
			}
			ByteArrayInputStream(response.body())
		} catch (ex: IOException) {
			log.error(ex.message)
			null
		}
	}
}
