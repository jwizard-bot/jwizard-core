package pl.jwizard.jwc.vote

import pl.jwizard.jwc.core.i18n.source.I18nResponseSource

class I18nVoterResponse private constructor(
	val initMessage: I18nMessageWithArgs<I18nResponseSource>,
	val failedMessage: I18nMessageWithArgs<I18nResponseSource>,
) {
	class Builder {
		private lateinit var initMessage: I18nMessageWithArgs<I18nResponseSource>
		private lateinit var failedMessage: I18nMessageWithArgs<I18nResponseSource>

		fun setInitMessage(
			i18nLocaleSource: I18nResponseSource,
			args: Map<String, Any?> = emptyMap(),
		) = apply {
			initMessage = I18nMessageWithArgs(i18nLocaleSource, args)
		}

		fun setFailedMessage(
			i18nLocaleSource: I18nResponseSource,
			args: Map<String, Any?> = emptyMap(),
		) = apply {
			failedMessage = I18nMessageWithArgs(i18nLocaleSource, args)
		}

		fun build() = I18nVoterResponse(initMessage, failedMessage)
	}
}
