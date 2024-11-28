/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.vote

import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse

/**
 * Interface representing content that processes the results of a successful vote. Defines a callback method to handle
 * actions after a successful operation.
 *
 * @param T The type of the payload that will be processed.
 * @author Miłosz Gilga
 */
interface VoterContent<T> {

	/**
	 * Called after a successful vote or operation, to perform any necessary actions or updates.
	 *
	 * @param context The context of the command that initiated the voting process.
	 * @param response The response that was generated after the vote was successfully processed.
	 * @param payload The payload that contains the relevant data for the successful operation.
	 */
	fun afterSuccess(context: CommandContext, response: TFutureResponse, payload: T)
}
