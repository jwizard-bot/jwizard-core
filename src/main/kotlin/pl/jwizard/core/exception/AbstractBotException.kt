/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.exception

import kotlin.reflect.KClass
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.log.JdaLogger
import org.slf4j.LoggerFactory

abstract class AbstractBotException(
	val event: CompoundCommandEvent?,
	clazz: KClass<*>,
	val i18nLocale: I18nExceptionLocale,
	val variables: Map<String, Any>,
	logMessage: String,
) : RuntimeException() {

	init {
		if (logMessage.isNotBlank()) {
			val logger = LoggerFactory.getLogger(clazz.java)
			if (event == null) {
				logger.error(logMessage)
			} else {
				JdaLogger(logger).error(event, logMessage)
			}
		}
	}
	
	constructor(
		event: CompoundCommandEvent?,
		clazz: KClass<*>,
		i18nLocale: I18nExceptionLocale,
		logMessage: String,
	) : this(event, clazz, i18nLocale, emptyMap(), logMessage)

	constructor(
		clazz: KClass<*>,
		i18nLocale: I18nExceptionLocale,
		logMessage: String,
	) : this(null, clazz, i18nLocale, emptyMap(), logMessage)
}
