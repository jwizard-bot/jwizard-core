/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.arg

import kotlin.reflect.KClass

enum class ArgumentTypeCaster(
	val clazz: KClass<*>,
	val castCallback: (rawValue: String) -> Any,
) {
	STRING(String::class, { it }),
	INTEGER(Int::class, { it.toInt() }),
	MENTIONABLE(String::class, { if (it.contains("@")) it.replace(Regex("<@|>"), "") else it }),
	CHANNEL(String::class, { if (it.contains("#")) it.replace(Regex("<#|>"), "") else it }),
	BOOLEAN(Boolean::class, { it.toBoolean() }),
	;
}
