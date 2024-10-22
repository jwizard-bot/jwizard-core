/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.integrity

import pl.jwizard.jwl.IrreparableException
import kotlin.reflect.KClass

/**
 * Exception thrown when there is a data integrity violation between local and remote data sources. This typically
 * indicates that the expected contract between the two sources has not been upheld.
 *
 * @property clazz The class where the violation occurred, providing context for the exception.
 * @property contractScheme A description of the expected contract that has been violated. This helps in identifying
 *           what was expected versus what was found.
 * @author Miłosz Gilga
 */
class DataIntegrityViolationException(
	private val clazz: KClass<*>,
	private val contractScheme: String,
) : IrreparableException(
	clazz = clazz,
	messageContent = "Contract in \"%s\" between local and remote sources not obtained.",
	args = arrayOf(contractScheme)
)
