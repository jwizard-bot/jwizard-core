/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse

/**
 * Defines the structure for handling commands in the application.
 *
 * This interface provides methods to execute commands and determine their scope (ex. private or public execution).
 * Implementing classes are expected to define the specific logic for processing commands and handling responses.
 *
 * @author Miłosz Gilga
 */
interface GuildCommandHandler {

	/**
	 * Executes the command logic and returns the command response.
	 *
	 * @param context The context of the command execution.
	 * @param response
	 */
	fun execute(context: GuildCommandContext, response: TFutureResponse)

	/**
	 * Determines if the command should be executed in a private context. This method can be overridden by subclasses to
	 * specify if the command is intended to be used in a private message context.
	 *
	 * @param context The context of the command execution.
	 * @return An optional value indicating the user ID (message receiver) if the command is private, or null if it is not.
	 */
	fun isPrivate(context: GuildCommandContext): Long? = null
}
