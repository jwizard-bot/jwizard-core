/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

/**
 * Enum representing the different types of commands that can be executed.
 *
 * @author Miłosz Gilga
 */
enum class CommandType {

	/**
	 * Represents a legacy command type, typically used with prefixes.
	 */
	PREFIX,

	/**
	 * Represents a slash command type, utilized in newer Discord interactions.
	 */
	SLASH,
	;
}
