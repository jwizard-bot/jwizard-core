/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback

import org.springframework.context.ApplicationContext
import pl.jwizard.core.radioplayback.rmf.RmfRadioStationPlaybackFetcher
import kotlin.reflect.KClass

enum class RadioStationPlayback(
	private val stationSlugs: List<String>,
	private val playbackFetcherClazz: KClass<out RadioStationPlaybackFetcher>
) {
	RMF(listOf("rmf-fm", "rmf-maxx"), RmfRadioStationPlaybackFetcher::class),
	;

	companion object {
		fun getBeanBaseSlug(
			applicationContext: ApplicationContext,
			stationSlug: String
		): RadioStationPlaybackFetcher? {
			val stationPlayback = entries.find { it.stationSlugs.contains(stationSlug) } ?: return null
			return applicationContext.getBean(stationPlayback.playbackFetcherClazz.java)
		}
	}
}
