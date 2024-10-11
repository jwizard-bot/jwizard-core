/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource.bind

import org.springframework.stereotype.Component
import pl.jwizard.jwc.audio.spi.RadioStationThumbnailSupplier
import pl.jwizard.jwc.core.util.ext.fromCamelToDashCase
import pl.jwizard.jwc.persistence.resource.ResourceObject

/**
 * A Spring component that supplies the thumbnail URL for a radio station. It implements the
 * [RadioStationThumbnailSupplier] interface, converting a given slug (station identifier) into the appropriate URL
 * format.
 *
 * @author Miłosz Gilga
 */
@Component
class RadioStationThumbnailSupplierBean : RadioStationThumbnailSupplier {

	/**
	 * Retrieves the thumbnail URL for a radio station by formatting the provided slug. The slug is converted from
	 * camelCase to dash-case to match the expected URL structure.
	 *
	 * @param slug The identifier of the radio station, typically in camelCase format.
	 * @return The formatted URL of the radio station's thumbnail image.
	 */
	override fun getThumbnailUrl(slug: String) =
		ResourceObject.RADIO_STATION.resourcePath.format(slug.fromCamelToDashCase)
}
