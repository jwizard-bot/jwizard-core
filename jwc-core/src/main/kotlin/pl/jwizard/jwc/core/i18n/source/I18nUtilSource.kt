/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for various utility-related messages.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 * @see I18nBean
 */
enum class I18nUtilSource(override val placeholder: String) : I18nLocaleSource {
	REQUIRED("jwc.util.required"),
	OPTIONAL("jwc.util.optional"),
	BUG_TRACKER("jwc.util.bugTracker"),
	COMPILATION_VERSION("jwc.util.compilationVersion"),
	TURN_ON("jwc.util.turnOn"),
	TURN_OFF("jwc.util.turnOff"),
	DATA_COMES_FROM("jwc.util.dataComesFrom"),
	;
}
