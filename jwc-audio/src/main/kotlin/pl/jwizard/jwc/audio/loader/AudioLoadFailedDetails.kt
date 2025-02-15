package pl.jwizard.jwc.audio.loader

import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class AudioLoadFailedDetails private constructor(
	val logMessage: String,
	val i18nLocaleSource: I18nExceptionSource,
	val logArguments: List<Any?>,
	val i18nArguments: Map<String, Any?>,
) {

	class Builder {
		private lateinit var logMessage: String
		private lateinit var i18nLocaleSource: I18nExceptionSource
		private var logArguments: List<Any?> = emptyList()
		private var i18nArguments: Map<String, Any?> = emptyMap()

		fun setLogMessage(logMessage: String, vararg args: Any?) = apply {
			this.logMessage = logMessage
			this.logArguments = args.toList()
		}

		fun setI18nLocaleSource(
			i18nLocaleSource: I18nExceptionSource,
			args: Map<String, Any?> = emptyMap(),
		) = apply {
			this.i18nLocaleSource = i18nLocaleSource
			this.i18nArguments = args
		}

		fun build() = AudioLoadFailedDetails(logMessage, i18nLocaleSource, logArguments, i18nArguments)
	}
}
