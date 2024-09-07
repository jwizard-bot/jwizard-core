/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import pl.jwizard.jwc.core.IrreparableException
import kotlin.reflect.KClass

/**
 * Exception thrown when a property cannot be found or accessed.
 *
 * This exception is used to indicate that a specific property identified by [propertyKey] was not found or could not
 * be accessed within the context of the given [clazz].
 *
 * @property clazz The class where the property was expected to be found or accessed.
 * @property propertyKey The key of the property that was not found or could not be accessed.
 *
 * @constructor Creates a new instance of [PropertyNotFoundException].
 *
 * @param clazz The class context where the property was expected.
 * @param propertyKey The key of the property that could not be found.
 *
 * @author Miłosz Gilga
 */
class PropertyNotFoundException(
	private val clazz: KClass<*>,
	private val propertyKey: String,
) : IrreparableException(
	clazz = clazz,
	messageContent = "Selected property: \"%s\" not found or could not be accessed.",
	args = arrayOf(propertyKey)
)
