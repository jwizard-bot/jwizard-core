package pl.jwizard.jwc.exception

import org.slf4j.LoggerFactory
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.jdaError
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

abstract class CommandPipelineException(
	val commandBaseContext: CommandBaseContext? = null,
	val i18nExceptionSource: I18nExceptionSource,
	val args: Map<String, Any?> = emptyMap(),
	val logMessage: String? = null,
) : RuntimeException() {

	private val log = LoggerFactory.getLogger(this::class.java)

	fun printLogStatement() {
		if (logMessage == null) {
			return
		}
		val message = logMessage.lineSequence().map { it.trim() }.joinToString(" ")
		if (commandBaseContext != null) {
			log.jdaError(commandBaseContext, message)
		} else {
			log.error(message)
		}
	}
}
