/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.async

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.core.jda.command.CommandResponse

/**
 * This interface defines hooks for handling the outcome of asynchronous operations. It provides methods to process both
 * success and failure cases of the operation, with the ability to respond to the command context.
 *
 * @param T The type of the payload passed to the async operation.
 * @author Miłosz Gilga
 */
interface AsyncUpdatableHook<T> {

	/**
	 * This method is called when the asynchronous operation completes successfully. It allows the implementer to handle
	 * the result and generate a response message to be displayed in the command context.
	 *
	 * @param context The context in which the command is executed.
	 * @param payload Additional information or context for handling the result.
	 * @return A [MessageEmbed] object containing the success message to be displayed.
	 */
	fun onAsyncSuccess(context: CommandContext, payload: T): MessageEmbed

	/**
	 * This method is called when the asynchronous operation fails. It allows the implementer to handle the error
	 * and optionally generate a response to be displayed. If not overridden, it returns general exception message.
	 *
	 * @param context The context in which the command is executed.
	 * @param throwable The error encountered during the async operation.
	 * @param payload Additional information or context for handling the error.
	 * @return A [CommandResponse] object representing the error message, or `null` if no response is provided.
	 */
	fun onFailedUpdate(context: CommandContext, throwable: Throwable, payload: T): CommandResponse? = null
}
