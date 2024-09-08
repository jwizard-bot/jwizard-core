/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.stereotype.AudioPlayerManager

/**
 * TODO
 *
 * @author Miłosz Gilga
 */
@Component
class AudioPlayerManagerBean : AudioPlayerManager {

	companion object {
		private val log = LoggerFactory.getLogger(AudioPlayerManagerBean::class.java)
	}

	/**
	 * TODO
	 *
	 */
	override fun registerSources() {
		log.info("register sources")
	}
}
