/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.spi

/**
 * Interface for supplying thumbnail URLs for radio stations. This interface allows retrieval of thumbnail images based
 * on a unique identifier (slug) representing the radio station.
 *
 * @author Miłosz Gilga
 */
interface RadioStationThumbnailSupplier {

	/**
	 * Retrieves the thumbnail URL for a radio station based on its slug.
	 *
	 * @param slug The unique identifier for the radio station, used to locate its thumbnail.
	 * @return The URL of the thumbnail image for the specified radio station.
	 */
	fun getThumbnailUrl(slug: String): String
}
