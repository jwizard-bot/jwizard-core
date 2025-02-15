package pl.jwizard.jwc.command.context

import pl.jwizard.jwc.command.exception.CommandParserException
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwl.command.arg.Argument

abstract class ArgumentContext : CommandBaseContext {

	// command arguments, where key is argument enum and second is argument value
	val commandArguments: MutableMap<Argument, String?> = mutableMapOf()

	inline fun <reified T : Any> getArg(
		argument: Argument,
	) = getNullableArg<T>(argument) ?: throw CommandParserException()

	inline fun <reified T : Any> getNullableArg(argument: Argument) = try {
		val value = commandArguments[argument]
		if (value == null) null else argument.type.castTo(value) as T?
	} catch (ex: NumberFormatException) {
		throw CommandParserException()
	}
}
