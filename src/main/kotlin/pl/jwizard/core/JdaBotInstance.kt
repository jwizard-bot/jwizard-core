/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core

import org.slf4j.Logger
import org.springframework.stereotype.Component
import pl.jwizard.core.util.logger

@Component
class JdaBotInstance {
	companion object {
		private val log: Logger = logger<JdaBotInstance>()
	}

	fun start() {
		log.info("Bot is running...")
		while (true) {
			// listening
		}
	}
}
