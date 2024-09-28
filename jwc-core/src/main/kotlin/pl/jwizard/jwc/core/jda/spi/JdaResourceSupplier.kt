/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

/**
 * Interface for supplying JDA resources, such as logos and banners, typically used in the context
 * of the JDA (Java Discord API) framework. This interface provides methods for retrieving these resources.
 *
 * @author Miłosz Gilga
 */
interface JdaResourceSupplier {

	/**
	 * Retrieves the JDA logo as a pair containing the resource path and the logo data.
	 *
	 * @return A [Pair] where the first element is the resource path and the second element is the logo data as a
	 *         byte array, or `null` if the logo could not be retrieved.
	 */
	fun getLogo(): Pair<String, ByteArray>?

	/**
	 * Retrieves the JDA banner as a pair containing the resource path and the banner data.
	 *
	 * @return A [Pair] where the first element is the resource path and the second element is the banner data as a
	 *         byte array, or `null` if the banner could not be retrieved.
	 */
	fun getBanner(): Pair<String, ByteArray>?
}
