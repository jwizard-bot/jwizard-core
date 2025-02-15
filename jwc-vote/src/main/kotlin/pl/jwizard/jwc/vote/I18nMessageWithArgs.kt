package pl.jwizard.jwc.vote

import pl.jwizard.jwl.i18n.I18nLocaleSource

data class I18nMessageWithArgs<T : I18nLocaleSource>(
	val message: T,
	val args: Map<String, Any?>,
)
