package pl.jwizard.jwc.radio

import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.ioc.reflect.ClasspathScanner
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.radio.PlaybackProvider
import pl.jwizard.jwl.util.logger
import java.util.concurrent.ConcurrentHashMap

@SingletonComponent
class RadioPlaybackMappersCacheBean(
	private val ioCKtContextFactory: IoCKtContextFactory,
) : RadioPlaybackMappersCache {

	companion object {
		private val log = logger<RadioPlaybackMappersCacheBean>()

		// package used for scanning provider classes
		private const val SCANNING_SUBPACKAGE = "jwc.radio.mapper"
	}

	private val playbackComponents = ConcurrentHashMap<PlaybackProvider, RadioPlaybackMapperHandler>()
	private val scanner = ClasspathScanner(RadioPlaybackMapper::class, SCANNING_SUBPACKAGE)

	override fun loadRadioPlaybackClasses() {
		scanner.findComponents().forEach { (provider, clazz) ->
			playbackComponents[provider.value] = ioCKtContextFactory
				.getBean(clazz) as RadioPlaybackMapperHandler
		}
		log.info("Load: {} radio playback components from IoC context.", playbackComponents.size)
	}

	override fun getCachedByProvider(
		provider: PlaybackProvider?,
	) = provider?.let { playbackComponents[it] }
}
