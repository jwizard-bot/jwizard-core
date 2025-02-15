package pl.jwizard.jwc.audio.spi

import pl.jwizard.jwl.radio.RadioStation
import java.io.InputStream

interface RadioStationThumbnailSupplier {
	fun getThumbnailResource(radioStation: RadioStation): Pair<String?, InputStream?>
}
