/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

import kotlin.reflect.KClass

/**
 * Casts a string value to the specified type [T].
 *
 * The string value is cast to the target type based on the provided [KClass].
 *
 * @param T The target type to cast the value to.
 * @param value The string value to cast.
 * @param targetType The [KClass] representing the target type.
 * @return The cast value of type [T].
 */
inline fun <reified T : Any> castToValue(value: String, targetType: KClass<*>): T {
	return when (targetType) {
		Int::class -> value.toInt() as T
		Double::class -> value.toDouble() as T
		Boolean::class -> value.toBoolean() as T
		Long::class -> value.toLong() as T
		else -> value as T
	}
}
