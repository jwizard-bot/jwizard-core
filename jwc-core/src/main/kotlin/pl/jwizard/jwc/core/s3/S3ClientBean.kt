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
import software.amazon.awssdk.services.s3.model.ListBucketsRequest
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI

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
		val resourceUrl = "$s3PublicApiUrl/${s3Object.resourcePath.format(args)}"
		return try {
			val resourceBytes = restTemplate.getForObject(resourceUrl, ByteArray::class.java)
			resourceBytes?.let { ByteArrayInputStream(it) }
		} catch (ex: RestClientException) {
			log.error(ex.message)
			null
		}
	}

	/**
	 * Shutdown and destroy [S3Client] after remove bean from Spring Context.
	 */
	override fun destroy() = client.close()
}
