package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwl.i18n.I18nLocaleSource

enum class I18nUtilSource(override val placeholder: String) : I18nLocaleSource {
	REQUIRED("jw.util.required"),
	OPTIONAL("jw.util.optional"),
	BUG_TRACKER("jw.util.bugTracker"),
	COMPILATION_VERSION("jw.util.compilationVersion"),
	DATA_COMES_FROM("jw.util.dataComesFrom"),
	WEBSITE("jw.util.website"),
	TURN_ON("jw.util.turnOn"),
	TURN_OFF("jw.util.turnOff"),
	;
}
