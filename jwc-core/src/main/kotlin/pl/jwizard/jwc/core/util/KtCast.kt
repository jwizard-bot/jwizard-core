/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

import kotlin.reflect.KClass

/**
 * Utility singleton for casting string values to various types. Provides functions to convert a string to a specific
 * type based on either a [KClass] or a type name.
 *
 * @author Miłosz Gilga
 */
object KtCast {

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

	/**
	 * Casts a string value to a type based on the provided type name.
	 *
	 * The method converts the string value to a type based on the string type name. Supports type names like "Int",
	 * "Double", "Long", and "Boolean". For any other type names, the string value is returned as is.
	 *
	 * @param value The string value to cast.
	 * @param targetName The name of the target type (ex. "Int", "Double", "Long", "Boolean").
	 * @return The cast value. Returns the original string if the type name is unrecognized.
	 */
	fun castToValue(value: String, targetName: String): Any {
		return when (targetName) {
			"Int" -> value.toInt()
			"Double" -> value.toDouble()
			"Long" -> value.toLong()
			"Boolean" -> value.toBoolean()
			else -> value
		}
	}
}
