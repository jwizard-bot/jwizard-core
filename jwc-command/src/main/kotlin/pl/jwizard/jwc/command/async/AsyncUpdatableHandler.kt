/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.async

import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwc.exception.ExceptionTrackerHandlerBean
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.util.logger
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

/**
 * This class is responsible for handling asynchronous updates in a command context. It uses a hook to handle success
 * and failure scenarios after an async action completes. The handler provides a structured response using the provided
 * context, response, and exception handling mechanisms.
 *
 * @param R The type of the result returned by the async operation.
 * @param I The type of the [Mono] that contains the async result.
 * @param P The type of the payload passed to the async operation.
 * @property context The context in which the command is executed.
 * @property response The future response that will be completed once the async update is done.
 * @property invokerClazz The class invoking this handler (used for logging and tracking).
 * @property asyncUpdatableHook The hook used to handle success or failure of the async operation.
 * @property exceptionTrackerHandler The store used to track and log exceptions.
 * @author Miłosz Gilga
 */
class AsyncUpdatableHandler<R, I : Mono<R>, P>(
	private val context: CommandContext,
	private val response: TFutureResponse,
	private val invokerClazz: KClass<*>,
	private val asyncUpdatableHook: AsyncUpdatableHook<R, I, P>,
	private val exceptionTrackerHandler: ExceptionTrackerHandlerBean,
) {

	companion object {
		private val log = logger<AsyncUpdatableHandler<*, *, *>>()
	}

	/**
	 * Executes an asynchronous update using the provided [Mono] action. The hook is used to handle both success and failure
	 * outcomes. In case of failure, the error is logged and handled accordingly.
	 *
	 * @param asyncAction The Mono representing the asynchronous action.
	 * @param payload The additional payload that may be needed for handling the response.
	 */
	fun performAsyncUpdate(asyncAction: I, payload: P) {
		asyncAction.subscribe({ onSuccess(it, payload) }, { onError(it, payload) })
	}

	/**
	 * Called when the asynchronous operation completes successfully. It delegates to the hook to process the result and
	 * create a response message, which is then used to complete the future response.
	 *
	 * @param result The result of the async operation.
	 * @param payload Additional information or context for handling the result.
	 */
	private fun onSuccess(result: R, payload: P) {
		val futureMessage = try {
			val responseMessage = asyncUpdatableHook.onAsyncSuccess(context, result, payload)
			CommandResponse.Builder()
				.addEmbedMessages(responseMessage)
				.build()
		} catch (ex: UnexpectedException) {
			ex.printLogStatement()
			createErrorResponse()
		}
		response.complete(futureMessage)
	}

	/**
	 * Called when the asynchronous operation encounters an error. The method logs the error and creates an appropriate
	 * error response by delegating to the hook, if available, or falling back to a default error response.
	 *
	 * @param throwable The error encountered during the async operation.
	 * @param payload Additional information or context for handling the error.
	 */
	private fun onError(throwable: Throwable, payload: P) {
		val errorResponse = asyncUpdatableHook.onFailedUpdate(context, throwable, payload)
		val message = errorResponse ?: createErrorResponse()
		log.jdaError(
			context,
			"Unable to update async action from: %s. Cause: %s.",
			invokerClazz.qualifiedName,
			throwable.message,
		)
		response.complete(message)
	}

	/**
	 * Creates a default error response when an unexpected exception occurs during the async operation. It utilizes an
	 * internationalized exception source to generate the error message and tracker link.
	 *
	 * @return A [CommandResponse] representing the error state.
	 */
	private fun createErrorResponse(): CommandResponse {
		val i18nSource = I18nExceptionSource.UNEXPECTED_EXCEPTION
		return CommandResponse.Builder()
			.addEmbedMessages(exceptionTrackerHandler.createTrackerMessage(i18nSource, context))
			.addActionRows(exceptionTrackerHandler.createTrackerLink(i18nSource, context))
			.build()
	}
}
