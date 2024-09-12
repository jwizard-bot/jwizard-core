/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.s3

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListBucketsRequest
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI
import java.nio.charset.Charset

/**
 * This class serves as a bean for interacting with an S3-compatible storage service. It manages the connection to
 * the S3 service and provides functionality to retrieve public objects via the S3 public API.
 *
 * @property environmentBean A Spring-managed bean that provides environment-specific properties
 *                           such as S3 access keys, region, and host URL.
 * @property restTemplate An instance of [RestTemplate] used to make HTTP requests to the S3 public API.
 * @author Miłosz Gilga
 * @see S3Client
 */
@Component
class S3ClientBean(
	private val environmentBean: EnvironmentBean,
	private val restTemplate: RestTemplate
) : DisposableBean {

	companion object {
		private val log = LoggerFactory.getLogger(S3ClientBean::class.java)
	}

	/**
	 * The S3 client that handles operations like listing buckets and accessing files.
	 */
	private final val client: S3Client

	/**
	 * The root bucket name, retrieved from the environment configuration, which serves as the primary storage container
	 * for files.
	 */
	private final val rootBucket: String

	/**
	 * The base URL for accessing the public S3 API, used to construct the complete URL for fetching public resources.
	 */
	private final val s3PublicApiUrl: String

	init {
		val hostUrl = environmentBean.getProperty<String>(BotProperty.S3_HOST_URL)

		val credentials = AwsBasicCredentials.create(
			environmentBean.getProperty(BotProperty.S3_ACCESS_KEY),
			environmentBean.getProperty(BotProperty.S3_SECRET_KEY)
		)
		client = S3Client.builder()
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.region(Region.of(environmentBean.getProperty(BotProperty.S3_REGION)))
			.endpointOverride(URI.create(hostUrl))
			.forcePathStyle(environmentBean.getProperty<Boolean>(BotProperty.S3_PATH_STYLE_ACCESS_ENABLED))
			.build()

		rootBucket = environmentBean.getProperty(BotProperty.S3_ROOT_BUCKET)
		s3PublicApiUrl = environmentBean.getProperty(BotProperty.S3_PUBLIC_API_URL)

		val buckets = client.listBuckets(ListBucketsRequest.builder().build()).buckets().map { it.name() }
		log.info("Connect with S3 host: {}. Public API url: {}. Buckets: {}.", hostUrl, s3PublicApiUrl, buckets)
	}

	/**
	 * Fetches a public object from S3 based on the provided [S3Object] and optional formatting arguments. This method
	 * constructs a URL to the public resource using the S3 public API and attempts to fetch the resource as a byte
	 * array, returning it as an [InputStream].
	 *
	 * If an error occurs during the request (ex. the resource is not found or the connection fails), the exception is
	 * caught, an error is logged, and `null` is returned.
	 *
	 * @param s3Object The object to fetch from the S3 bucket.
	 * @param args Optional arguments to format the resource path.
	 * @return The resource as an [InputStream], or `null` if the resource cannot be retrieved.
	 */
	fun getPublicObject(s3Object: S3Object, vararg args: String): InputStream? {
		val resourceUrl = "$s3PublicApiUrl/${parseResourcePath(s3Object, *args)}"
		return try {
			val resourceBytes = restTemplate.getForObject(resourceUrl, ByteArray::class.java)
			resourceBytes?.let { ByteArrayInputStream(it) }
		} catch (ex: RestClientException) {
			log.error(ex.message)
			null
		}
	}

	/**
	 * Retrieves a private object from the S3 bucket using the [S3Client]. This method constructs an S3 object request
	 * based on the provided [S3Object] and optional formatting arguments, and returns the content as an [InputStream].
	 *
	 * @param s3Object The object to fetch from the S3 bucket.
	 * @param args Optional arguments to format the resource path.
	 * @return The object as an [InputStream], or `null` if the object cannot be retrieved.
	 */
	fun getObject(s3Object: S3Object, vararg args: String): InputStream? {
		val objectRequest = GetObjectRequest.builder()
			.bucket(rootBucket)
			.key(parseResourcePath(s3Object, *args))
			.build()
		return client.getObject(objectRequest)
	}

	/**
	 * Retrieves the content of an object from the S3 bucket as a text string. This method uses the [getObject] function
	 * to fetch the object as an [InputStream], then reads the content using the specified [charset].
	 *
	 * @param s3Object The object to fetch from the S3 bucket.
	 * @param charset The character set to use when decoding the content of the object.
	 * @param args Optional arguments to format the resource path.
	 * @return The content of the object as a [String], or `null` if the object cannot be retrieved or read.
	 */
	fun getObjectAsText(s3Object: S3Object, charset: Charset, vararg args: String): String? {
		val inputStream = getObject(s3Object, *args)
		return inputStream?.bufferedReader(charset).use { it?.readText() }
	}

	/**
	 * Parses the resource path for the given [S3Object], replacing any placeholders in the resource path with the
	 * provided arguments.
	 *
	 * @param s3Object The S3 object whose resource path is to be parsed.
	 * @param args The arguments to format the resource path.
	 * @return A string representing the formatted resource path.
	 */
	private fun parseResourcePath(s3Object: S3Object, vararg args: String) = s3Object.resourcePath.format(*args)

	/**
	 * Shutdown and destroy [S3Client] after remove bean from Spring Context.
	 */
	override fun destroy() = client.close()
}
