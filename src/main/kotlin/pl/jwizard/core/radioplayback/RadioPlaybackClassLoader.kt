/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback

import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class RadioPlaybackClassLoader(
	private val applicationContext: ApplicationContext,
) {
	companion object {
		private const val DELIMITER = "+"
	}

	fun loadClass(stationSlug: String): RadioStationPlaybackFetcher? {
		// get bean definition based component name (multiple choices splitted by DELIMITER)
		val playbackClassDefinition = applicationContext.beanDefinitionNames
			.find { it.split(DELIMITER).contains(stationSlug) }
			?: return null
		return applicationContext.getBean(playbackClassDefinition, RadioStationPlaybackFetcher::class.java)
	}
}
