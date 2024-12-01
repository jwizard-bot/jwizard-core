/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.context.GlobalCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse

/**
 * Represents a handler for executing global commands.
 *
 * A global command is not tied to a specific guild and can typically be executed in direct messages (DMs) or in a
 * broader context outside a guild.
 *
 * @author Miłosz Gilga
 */
interface GlobalCommandHandler {

	/**
	 * Executes a global command based on the provided context and response handling mechanism.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param response The future response object used to send the result of the command execution.
	 */
	fun executeGlobal(context: GlobalCommandContext, response: TFutureResponse)
}
