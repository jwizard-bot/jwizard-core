/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwl.i18n.I18nLocaleSource

/**
 * Provides internationalization (i18n) placeholders for various action-related messages.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 */
enum class I18nActionSource(override val placeholder: String) : I18nLocaleSource {
	REFRESH("jw.action.refresh"),
	DETAILS("jw.action.details"),
	FIRST("jw.action.first"),
	PREV("jw.action.prev"),
	NEXT("jw.action.next"),
	LAST("jw.action.last"),
	YES("jw.action.yes"),
	NO("jw.action.no"),
	STATUS("jw.action.status"),
	;
}
