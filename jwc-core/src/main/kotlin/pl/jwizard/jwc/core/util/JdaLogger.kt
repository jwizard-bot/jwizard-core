/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

import org.slf4j.Logger
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.ext.qualifier

/**
 * Extension function for logging info messages with context.
 *
 * @param context The context for the command, providing guild and author information.
 * @param message The message to log.
 * @param args Additional arguments for message formatting.
 * @author Miłosz Gilga
 */
fun Logger.jdaInfo(context: CommandBaseContext, message: String, vararg args: Any?) {
	info(loggerMessageContent(context, message, *args))
}

/**
 * Extension function for logging debug messages with context.
 *
 * @param context The context for the command, providing guild and author information.
 * @param message The message to log.
 * @param args Additional arguments for message formatting.
 * @author Miłosz Gilga
 */
fun Logger.jdaDebug(context: CommandBaseContext, message: String, vararg args: Any?) {
	debug(loggerMessageContent(context, message, *args))
}

/**
 * Extension function for logging error messages with context.
 *
 * @param context The context for the command, providing guild and author information.
 * @param message The message to log.
 * @param args Additional arguments for message formatting.
 * @author Miłosz Gilga
 */
fun Logger.jdaError(context: CommandBaseContext, message: String, vararg args: Any?) {
	error(loggerMessageContent(context, message, *args))
}

/**
 * Helper function to format log messages with context details.
 *
 * @param commandBaseContext The context for the command, providing guild and author information.
 * @param message The message to format.
 * @param args Additional arguments for message formatting.
 * @return A formatted string containing guild, author, and the message.
 */
private fun loggerMessageContent(commandBaseContext: CommandBaseContext, message: String, vararg args: Any?) =
	"G: %s, A: %s -> %s".format(
		commandBaseContext.guild.qualifier,
		commandBaseContext.author.qualifier,
		message.format(*args)
	)
