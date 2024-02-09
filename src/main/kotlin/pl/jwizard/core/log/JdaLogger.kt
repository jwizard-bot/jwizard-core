/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.log

import pl.jwizard.core.command.CompoundCommandEvent
import org.slf4j.Logger

class JdaLogger(private val logger: Logger) {
	fun info(event: CompoundCommandEvent, message: String) {
		logger.info("G: {}, A: {} > {}", event.guildName, event.authorTag, message)
	}

	fun error(event: CompoundCommandEvent, message: String) {
		logger.error("G: {}, A: {} > {}", event.guildName, event.authorTag, message)
	}
}
