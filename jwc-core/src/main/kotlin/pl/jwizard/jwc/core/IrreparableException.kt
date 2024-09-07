/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName
import kotlin.system.exitProcess

/**
 * Exception class defining irreparable issues, which are critical problems that will terminate the whole
 * application if encountered.
 *
 * This exception is used to indicate severe errors that require the application to stop immediately.
 *
 * @property clazz The class from which this exception originated.
 * @property messageContent The message template in [String] format used to describe the exception.
 * @property args Additional arguments to format the [messageContent].
 * @constructor Create an exception with the specified [clazz], [messageContent], and optional [args].
 * @author Miłosz Gilga
 */
open class IrreparableException(
	private val clazz: KClass<*>,
	private val messageContent: String = "",
	private vararg val args: Any,
) : RuntimeException(String.format(messageContent, *args)) {

	/**
	 * A list containing the causes (messages) of the stack trace for this exception.
	 *
	 * The causes are added to this list during the exception's creation and initialization from an existing exception.
	 * Each cause is represented as a string containing the class name and error message.
	 */
	private val stacktraceCauses = mutableListOf<String>()

	init {
		val message = String.format(messageContent, *args)
		if (message.isNotEmpty()) {
			stacktraceCauses.add(message)
		}
	}

	/**
	 * Constructs an instance of [IrreparableException] based on the provided [Throwable].
	 * This constructor initializes the stack trace causes by traversing the causes of the given [throwable].
	 *
	 * @param throwable The original [Throwable] from which to derive the stack trace causes.
	 */
	constructor(throwable: Throwable) : this(DiscordBotAppRunner::class) {
		var cause: Throwable? = throwable.cause
		while (cause != null) {
			stacktraceCauses.add("[${clazz.jvmName} -> ${cause.javaClass.name}]: ${cause.message}")
			cause = cause.cause
		}
	}

	/**
	 * Logs the stack trace causes of this exception using the SLF4J logger. Each cause is logged at the error level.
	 */
	fun printLogStatement() {
		val log = LoggerFactory.getLogger(clazz.java)
		stacktraceCauses.forEach { log.error(it) }
	}

	/**
	 * Terminates the current JVM process with a non-zero exit code (1). This method is used to forcefully stop the
	 * application when an irreparable issue is encountered.
	 */
	fun killProcess() {
		exitProcess(1)
	}
}
