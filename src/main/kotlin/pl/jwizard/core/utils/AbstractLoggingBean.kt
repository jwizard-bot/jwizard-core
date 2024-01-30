/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.utils

import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractLoggingBean(
	loggerClazz: KClass<*>
) {
	protected val log: Logger = LoggerFactory.getLogger(loggerClazz.java)
}
