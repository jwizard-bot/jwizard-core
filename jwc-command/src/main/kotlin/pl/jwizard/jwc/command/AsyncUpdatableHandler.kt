package pl.jwizard.jwc.command

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwc.exception.ExceptionTrackerHandler
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.util.logger
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

class AsyncUpdatableHandler(
	private val context: CommandBaseContext,
	private val response: TFutureResponse,
	private val invokerClazz: KClass<*>,
	exceptionTrackerHandler: ExceptionTrackerHandler,
) {
	companion object {
		private val log = logger<AsyncUpdatableHandler>()
	}

	private val i18nSource = I18nExceptionSource.UNEXPECTED_EXCEPTION

	private val defaultErrorResponse = CommandResponse.Builder()
		.addEmbedMessages(exceptionTrackerHandler.createTrackerMessage(i18nSource, context))
		.addActionRows(exceptionTrackerHandler.createTrackerLink(i18nSource, context))
		.build()

	fun performAsyncUpdate(
		asyncAction: Mono<*>,
		onSuccess: () -> MessageEmbed,
		onFailure: (t: Throwable) -> CommandResponse? = { null },
	) {
		asyncAction.subscribe({
			// on success
			val futureMessage = try {
				val responseMessage = onSuccess()
				CommandResponse.Builder()
					.addEmbedMessages(responseMessage)
					.build()
			} catch (ex: UnexpectedException) {
				ex.printLogStatement()
				defaultErrorResponse
			}
			response.complete(futureMessage)
		}, {
			// on error
			val errorResponse = onFailure(it)
			val message = errorResponse ?: defaultErrorResponse
			log.jdaError(
				context,
				"Unable to update async action from: %s. Cause: %s.",
				invokerClazz.qualifiedName,
				it.message,
			)
			response.complete(message)
		})
	}
}
