/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.ext.authorQualifier
import pl.jwizard.jwc.core.util.ext.guildQualifier

/**
 * Creates a logger instance for the specified class type.
 *
 * This inline function utilizes Kotlin's reified type parameters to allow for type-safe logging, creating a logger
 * that is associated with the class type provided as the generic type parameter.
 *
 * @param T The type of the class for which the logger is being created.
 * @return A [Logger] instance associated with the specified class type.
 * @author Miłosz Gilga
 */
inline fun <reified T : Any> logger(): Logger = LoggerFactory.getLogger(T::class.java)

/**
 * Extension function for logging info messages with context.
 *
 * @param commandBaseContext The context for the command, providing guild and author information.
 * @param message The message to log.
 * @param args Additional arguments for message formatting.
 * @author Miłosz Gilga
 */
fun Logger.jdaInfo(commandBaseContext: CommandBaseContext, message: String, vararg args: Any) {
	info(loggerMessageContent(commandBaseContext, message, *args))
}

/**
 * Extension function for logging error messages with context.
 *
 * @param commandBaseContext The context for the command, providing guild and author information.
 * @param message The message to log.
 * @param args Additional arguments for message formatting.
 * @author Miłosz Gilga
 */
fun Logger.jdaError(commandBaseContext: CommandBaseContext, message: String, vararg args: Any) {
	error(loggerMessageContent(commandBaseContext, message, *args))
}

/**
 * Helper function to format log messages with context details.
 *
 * @param commandBaseContext The context for the command, providing guild and author information.
 * @param message The message to format.
 * @param args Additional arguments for message formatting.
 * @return A formatted string containing guild, author, and the message.
 */
private fun loggerMessageContent(commandBaseContext: CommandBaseContext, message: String, vararg args: Any) =
	"G: %s, A: %s -> %s".format(
		commandBaseContext.guildQualifier,
		commandBaseContext.authorQualifier,
		message.format(*args)
	)
