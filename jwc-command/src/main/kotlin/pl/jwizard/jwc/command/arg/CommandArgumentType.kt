/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.arg

/**
 * Represents the various types of command arguments and their conversion logic. Each type defines how a string value
 * should be cast to its appropriate data type.
 *
 * @property castTo A function that defines how to convert a string value to the corresponding type.
 * @author Miłosz Gilga
 */
enum class CommandArgumentType(val castTo: (value: String) -> Any) {

	/**
	 * Represents a string type.
	 * No conversion is needed; the value is returned as is.
	 */
	STRING({ it }),

	/**
	 * Represents an integer type.
	 * The string value is parsed as an integer using [String.toInt].
	 */
	INTEGER({ it.toInt() }),

	/**
	 * Represents a mentionable type (ex. users).
	 * If the value contains a mention format, it strips the mention tags.
	 */
	MENTIONABLE({ (if (it.contains("@")) it.replace(Regex("<@|>"), "") else it).toLong() }),

	/**
	 * Represents a channel type.
	 * If the value contains a channel mention format, it strips the channel tags.
	 */
	CHANNEL({ (if (it.contains("#")) it.replace(Regex("<#|>"), "") else it).toLong() }),

	/**
	 * Represents a boolean type.
	 * The string value is converted to a boolean using [String.toBoolean].
	 */
	BOOLEAN({ it.toBoolean() }),
	;
}
