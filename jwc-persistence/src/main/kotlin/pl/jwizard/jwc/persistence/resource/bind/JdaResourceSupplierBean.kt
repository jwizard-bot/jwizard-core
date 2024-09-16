/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource.bind

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.spi.JdaResourceSupplier
import pl.jwizard.jwc.persistence.resource.HttpResourceRetrieverBean
import pl.jwizard.jwc.persistence.resource.ResourceObject

/**
 * This class is responsible for providing JDA-specific resources, such as logos and banners, by fetching them from an
 * HTTP resource via [HttpResourceRetrieverBean].
 *
 * @property httpResourceRetrieverBean Injected bean responsible for retrieving resources via HTTP.
 * @author Miłosz Gilga
 */
@Component
class JdaResourceSupplierBean(
	private val httpResourceRetrieverBean: HttpResourceRetrieverBean
) : JdaResourceSupplier {

	/**
	 * Retrieves the JDA logo from HTTP resource.
	 *
	 * @return A [Pair] where the first element is the resource path and the second element is the logo data as a
	 * 				 byte array, or `null` if the resource could not be retrieved.
	 */
	override fun getLogo() = getResource(ResourceObject.LOGO)

	/**
	 * Retrieves the JDA banner from HTTP resource.
	 *
	 * @return A [Pair] where the first element is the resource path and the second element is the banner data as a
	 * 			   byte array, or `null` if the resource could not be retrieved.
	 */
	override fun getBanner() = getResource(ResourceObject.BANNER)

	/**
	 * Fetches a resource from HTTP storage based on the provided [ResourceObject].
	 *
	 * @param resourceObject The [ResourceObject] that specifies which resource to retrieve.
	 * @return A [Pair] where the first element is the resource path and the second element is the resource data as a
	 * 		     byte array, or `null` if the resource could not be retrieved.
	 */
	private fun getResource(resourceObject: ResourceObject): Pair<String, ByteArray>? {
		val data = httpResourceRetrieverBean.getObjectAsByteArray(resourceObject) ?: return null
		return Pair(resourceObject.resourcePath, data)
	}
}
