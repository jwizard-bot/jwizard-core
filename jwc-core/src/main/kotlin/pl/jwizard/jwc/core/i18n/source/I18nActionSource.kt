/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for various action-related messages.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 * @see I18nBean
 */
enum class I18nActionSource(override val placeholder: String) : I18nLocaleSource {
	REFRESH("jwc.action.refresh"),
	DETAILS("jwc.action.details"),
	FIRST("jwc.action.first"),
	PREV("jwc.action.prev"),
	NEXT("jwc.action.next"),
	LAST("jwc.action.last"),
	YES("jwc.action.yes"),
	NO("jwc.action.no"),
	;
}
