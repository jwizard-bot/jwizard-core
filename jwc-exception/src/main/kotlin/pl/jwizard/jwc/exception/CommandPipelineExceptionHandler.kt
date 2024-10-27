/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception

import org.slf4j.LoggerFactory
import pl.jwizard.jwc.core.exception.CommandPipelineException
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Base class for exceptions that occur within the command processing pipeline.
 *
 * This abstract class provides a structure for exceptions that are related to command execution in the application,
 * encapsulating relevant context information such as the command context and internationalization resources.
 *
 * @property commandBaseContext The context of the command being executed, which may contain relevant information for
 *           logging.
 * @property i18nExceptionSource Source for internationalized exception messages.
 * @property args A map of additional variables to be used for message formatting.
 * @property logMessage A custom message to log when the exception occurs.
 * @author Miłosz Gilga
 */
abstract class CommandPipelineExceptionHandler(
	override val commandBaseContext: CommandBaseContext? = null,
	override val i18nExceptionSource: I18nExceptionSource,
	override val args: Map<String, Any?> = emptyMap(),
	override val logMessage: String? = null,
) : RuntimeException(), CommandPipelineException {

	private val log = LoggerFactory.getLogger(this::class.java)

	/**
	 * Logs the exception message if provided.
	 *
	 * If a command base context is available, the log statement will include context-specific details. Otherwise, the
	 * error message will be logged at a general level.
	 */
	fun printLogStatement() {
		if (logMessage != null) {
			val message = logMessage!!.trimIndent()
			if (commandBaseContext != null) {
				log.jdaError(commandBaseContext!!, message)
			} else {
				log.error(message)
			}
		}
	}
}
