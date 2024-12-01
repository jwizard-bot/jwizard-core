/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.context

import pl.jwizard.jwc.command.exception.CommandParserException
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwl.command.arg.Argument

/**
 * Abstract class representing the context for handling parsed command arguments.
 *
 * This class extends [CommandBaseContext] and provides functionality for managing parsed command arguments, storing
 * them in a map, and retrieving them with the necessary type casting. It also handles errors related to argument
 * parsing and type casting.
 *
 * @author Miłosz Gilga
 */
abstract class ArgumentContext : CommandBaseContext {

	/**
	 * A mutable map that stores the parsed command arguments, with their corresponding [Argument] as keys and their
	 * parsed data as values.
	 */
	val commandArguments: MutableMap<Argument, String?> = mutableMapOf()

	/**
	 * Retrieves and casts the argument value for the specified [Argument].
	 *
	 * @param argument The [Argument] whose value is to be retrieved.
	 * @param T The type to cast the argument value to.
	 * @return The cast value of the argument.
	 * @throws CommandParserException If the argument is not found or cannot be cast to the desired type.
	 */
	inline fun <reified T : Any> getArg(argument: Argument) = getNullableArg<T>(argument)
		?: throw CommandParserException()

	/**
	 * Retrieves the argument value for the specified [Argument], returning null if not found.
	 *
	 * @param argument The [Argument] whose value is to be retrieved.
	 * @param T The type to cast the argument value to.
	 * @return The cast value of the argument, or null if not found.
	 * @throws CommandParserException If the argument cannot be cast to the desired type.
	 */
	inline fun <reified T : Any> getNullableArg(argument: Argument) = try {
		val value = commandArguments[argument]
		if (value == null) null else argument.type.castTo(value) as T?
	} catch (ex: NumberFormatException) {
		throw CommandParserException()
	}
}
