package pl.jwizard.jwc.persistence.resource.bind

import pl.jwizard.jwc.audio.spi.RadioStationThumbnailSupplier
import pl.jwizard.jwc.persistence.resource.ResourceObject
import pl.jwizard.jwc.persistence.resource.StaticClasspathRetrieverBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.radio.RadioStation

@SingletonComponent
class RadioStationThumbnailSupplierBean(
	private val staticClasspathRetriever: StaticClasspathRetrieverBean,
) : RadioStationThumbnailSupplier {

	override fun getThumbnailResource(
		radioStation: RadioStation,
	) = staticClasspathRetriever.getObject(
		ResourceObject.RADIO_STATION,
		radioStation.textKey,
	)
}
