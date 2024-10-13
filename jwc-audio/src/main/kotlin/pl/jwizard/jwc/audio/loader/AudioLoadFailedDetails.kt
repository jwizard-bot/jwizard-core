/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource

/**
 * Represents the details of a failed audio load operation.
 *
 * @property logMessage A message to be logged when the audio load fails.
 * @property i18nLocaleSource The internationalization source for localization of error messages.
 * @property logArguments The arguments to be used in the log message.
 * @property i18nArguments A map of arguments to be used for internationalized messages.
 * @author Miłosz Gilga
 */
class AudioLoadFailedDetails private constructor(
	val logMessage: String,
	val i18nLocaleSource: I18nExceptionSource,
	val logArguments: List<Any?>,
	val i18nArguments: Map<String, Any?>,
) {

	/**
	 * Builder for constructing instances of [AudioLoadFailedDetails].
	 */
	class Builder {

		/**
		 * The message to be logged upon a load failure.
		 */
		private lateinit var logMessage: String

		/**
		 * The internationalization source for error messages.
		 */
		private lateinit var i18nLocaleSource: I18nExceptionSource

		/**
		 * The list of arguments for the log message.
		 */
		private var logArguments: List<Any?> = emptyList()

		/**
		 * The map of arguments for internationalized messages.
		 */
		private var i18nArguments: Map<String, Any?> = emptyMap()

		/**
		 * Sets the log message and its arguments for the load failure details.
		 *
		 * @param logMessage The log message for the failure.
		 * @param args The arguments to be formatted into the log message.
		 * @return The builder instance for chaining.
		 */
		fun setLogMessage(logMessage: String, vararg args: Any?) = apply {
			this.logMessage = logMessage
			this.logArguments = args.toList()
		}

		/**
		 * Sets the internationalization source and arguments for the load failure details.
		 *
		 * @param i18nLocaleSource The internationalization source for error messages.
		 * @param args A map of arguments for internationalized messages.
		 * @return The builder instance for chaining.
		 */
		fun setI18nLocaleSource(i18nLocaleSource: I18nExceptionSource, args: Map<String, Any?> = emptyMap()) = apply {
			this.i18nLocaleSource = i18nLocaleSource
			this.i18nArguments = args
		}

		/**
		 * Builds an instance of [AudioLoadFailedDetails] using the current builder state.
		 *
		 * @return A new instance of [AudioLoadFailedDetails].
		 */
		fun build() = AudioLoadFailedDetails(logMessage, i18nLocaleSource, logArguments, i18nArguments)
	}
}
