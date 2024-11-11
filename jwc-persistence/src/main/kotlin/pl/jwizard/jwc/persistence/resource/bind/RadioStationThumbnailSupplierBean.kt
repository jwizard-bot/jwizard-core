/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource.bind

import pl.jwizard.jwc.audio.spi.RadioStationThumbnailSupplier
import pl.jwizard.jwc.persistence.resource.ResourceObject
import pl.jwizard.jwc.persistence.resource.StaticClasspathRetrieverBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.radio.RadioStation
import java.io.InputStream

/**
 * Implementation of the [RadioStationThumbnailSupplier] interface for supplying thumbnail resources  of various radio
 * stations. This bean uses [StaticClasspathRetrieverBean] to retrieve the required resources from the classpath.
 *
 * @property staticClasspathRetrieverBean Injected bean to manage the retrieval of classpath resources.
 * @author Miłosz Gilga
 */
@SingletonComponent
class RadioStationThumbnailSupplierBean(
	private val staticClasspathRetrieverBean: StaticClasspathRetrieverBean,
) : RadioStationThumbnailSupplier {

	/**
	 * Retrieves the thumbnail resource for a given [RadioStation].
	 *
	 * This method formats the radio station's text key (converted from camelCase to dash-case) and uses it to locate the
	 * appropriate thumbnail resource via [StaticClasspathRetrieverBean].
	 *
	 * @param radioStation The [RadioStation] whose thumbnail is to be retrieved.
	 * @return A [Pair] containing the path of the resource and its [InputStream], or `null` if the resource is
	 *         unavailable.
	 */
	override fun getThumbnailResource(radioStation: RadioStation) = staticClasspathRetrieverBean.getObject(
		ResourceObject.RADIO_STATION,
		radioStation.textKey,
	)
}
