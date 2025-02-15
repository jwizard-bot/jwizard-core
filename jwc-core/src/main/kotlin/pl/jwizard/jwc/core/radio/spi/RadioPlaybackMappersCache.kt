package pl.jwizard.jwc.core.radio.spi

import pl.jwizard.jwl.radio.PlaybackProvider

interface RadioPlaybackMappersCache {
	fun loadRadioPlaybackClasses()

	fun getCachedByProvider(provider: PlaybackProvider?): RadioPlaybackMessage?
}
