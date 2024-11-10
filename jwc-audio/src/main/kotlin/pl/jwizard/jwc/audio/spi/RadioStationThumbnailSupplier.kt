/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.spi

import pl.jwizard.jwl.radio.RadioStation
import java.io.InputStream

/**
 * Interface for supplying thumbnail resources for radio stations.
 *
 * This interface defines the contract for retrieving thumbnail images associated with specific radio stations.
 * Implementations should fetch the thumbnail as an [InputStream] and return it alongside a relevant identifier.
 *
 * @see RadioStation
 */
interface RadioStationThumbnailSupplier {

	/**
	 * Retrieves the thumbnail image resource for a given [RadioStation].
	 *
	 * This method takes a [RadioStation] instance and returns a [Pair] containing the name or identifier of the thumbnail
	 * (as a [String]) and an [InputStream] for accessing the image resource. If the resource is unavailable, both
	 * elements of the pair may be `null`.
	 *
	 * @param radioStation The [RadioStation] for which to fetch the thumbnail.
	 * @return A [Pair] where the first element is the thumbnail name or identifier, and the second is the [InputStream]
	 *         of the image resource, or `null` values if the resource is not available.
	 */
	fun getThumbnailResource(radioStation: RadioStation): Pair<String?, InputStream?>
}
