/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListBucketsRequest
import java.io.InputStream
import java.net.URI

/**
 * This class serves as a bean for interacting with an S3-compatible storage service.
 *
 * @property environmentBean A Spring-managed bean that provides environment-specific properties such as S3 access
 *           keys, region, and host URL.
 * @author Miłosz Gilga
 * @see S3Client
 * @see ResourceRetriever
 */
@Component
class S3ResourceRetrieverBean(private val environmentBean: EnvironmentBean) : ResourceRetriever(), DisposableBean {

	companion object {
		private val log = LoggerFactory.getLogger(S3ResourceRetrieverBean::class.java)
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

		val buckets = client.listBuckets(ListBucketsRequest.builder().build()).buckets().map { it.name() }
		log.info("Init S3 resource retriever. Connect with S3 host: {}. Buckets: {}.", hostUrl, buckets)
	}

	/**
	 * Fetches an object from S3 as an [InputStream].
	 *
	 * @param resourceObject Specifies which resource to retrieve.
	 * @param args Additional arguments to format the resource path.
	 * @return An [InputStream] containing the resource, or `null` if it could not be retrieved.
	 */
	override fun getObject(resourceObject: ResourceObject, vararg args: String): InputStream? {
		val objectRequest = GetObjectRequest.builder()
			.bucket(rootBucket)
			.key(parseResourcePath(resourceObject, *args))
			.build()
		return client.getObject(objectRequest)
	}

	/**
	 * Shutdown and destroy [S3Client] after remove bean from Spring Context.
	 */
	override fun destroy() = client.close()
}
